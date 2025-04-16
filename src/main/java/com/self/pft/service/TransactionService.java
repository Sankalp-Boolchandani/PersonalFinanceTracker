package com.self.pft.service;

import com.self.pft.entity.Transaction;
import com.self.pft.entity.User;
import com.self.pft.entity.request.TransactionRequest;
import com.self.pft.entity.response.TransactionResponse;
import com.self.pft.enums.TransactionType;
import com.self.pft.repository.TransactionRepository;
import com.self.pft.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Transaction> createTransaction(TransactionRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(()->new EntityNotFoundException("User not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setExpenseCategory(request.getExpenseCategory());

        Transaction saved = transactionRepository.save(transaction);
        checkIfBudgetEceeded(user);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    private void checkIfBudgetEceeded(User user) {
        if (user!=null){
            List<Transaction> transactionList = transactionRepository.findByUserId(user.getId());

//            // approach that I used
//            Map<TransactionType, BigDecimal> transactionsByType = transactionList.stream().collect(Collectors.groupingBy(
//                    Transaction::getTransactionType,
//                    Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
//            ));
//            BigDecimal expenses = transactionsByType.get(TransactionType.EXPENSE);

//          // suggested approach
            BigDecimal expenses=transactionList.stream().filter(x ->
                            x.getTransactionType().equals(TransactionType.EXPENSE))
                    .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (expenses!=null && user.getBudgetLimit()!=null && expenses.compareTo(user.getBudgetLimit()) > 0){
                log.warn("Budget exceeded! User "+user.getId()+" spent ₹"+expenses+" out of ₹"+user.getBudgetLimit());
            }
        }
    }

    public ResponseEntity<List<TransactionResponse>> getUserTransactionsByUserId(
            Long userId, TransactionType transactionType, LocalDateTime startDate, LocalDateTime endDate){

        userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("User not found"));

//        List<Transaction> transactionList = transactionRepository.findByUserId(userId);               // task 4: searching with filters along with userId. (userId is a necessary param)

        List<Transaction> transactionList = transactionRepository.findTransactionsByUserWithFilters(userId, transactionType, startDate, endDate);
        List<TransactionResponse> transactionResponseList=new ArrayList<>();
        if (transactionList!=null){
            for (Transaction transaction: transactionList){
                TransactionResponse response=new TransactionResponse();

                response.setId(transaction.getId());
                response.setAmount(transaction.getAmount());
                response.setDescription(transaction.getDescription());
                response.setTransactionType(transaction.getTransactionType());
                response.setTransactionDate(transaction.getTransactionDate());
                response.setCreatedAt(transaction.getCreatedAt());
                response.setUpdatedAt(transaction.getUpdatedAt());
                response.setExpenseCategory(transaction.getExpenseCategory());

                transactionResponseList.add(response);
            }
            return new ResponseEntity<>(transactionResponseList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    public ResponseEntity<String> deleteTransactionById(Long id) {
        try{
            Transaction transaction = transactionRepository.findById(id).get();
            transactionRepository.delete(transaction);
            return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>("Transaction not found with id: "+id, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> updateTransactionById(Long id, TransactionRequest transactionRequest){
        try {
            Transaction transaction = transactionRepository.findById(id).get();

            if (transactionRequest.getTransactionType()!=null){
                transaction.setTransactionType(transactionRequest.getTransactionType());
            }
            if (transactionRequest.getTransactionDate()!=null){
                transaction.setTransactionDate(transactionRequest.getTransactionDate());
            }
            if (transactionRequest.getDescription()!=null){
                transaction.setDescription(transactionRequest.getDescription());
            }
            if (transactionRequest.getAmount()!=null){
                transaction.setAmount(transactionRequest.getAmount());
            }
            if (transactionRequest.getExpenseCategory()!=null){
                transaction.setExpenseCategory(transactionRequest.getExpenseCategory());
            }

            transactionRepository.save(transaction);
            return new ResponseEntity<>("Transaction updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Transaction not found with id: "+id, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Map<String, BigDecimal>> getTransactionSummaryTotal(Long userId) {
        userRepository.findById(userId).orElseThrow(
                ()->new NoSuchElementException("User with id: "+userId+" not found"));
        List<Transaction> transactionList = transactionRepository.findByUserId(userId)
                .stream().filter(x->x.getTransactionType().equals(TransactionType.EXPENSE)).toList();
        if (!transactionList.isEmpty()){

//            // older code. not an appreciable practise now!!!!
//            Map<String, BigDecimal> transactionMap=new HashMap<>();
//            for (Transaction transaction: transactionList){
//                transactionMap.put(transaction.getDescription(),
//                        transactionMap.getOrDefault(transaction.getDescription(), BigDecimal.ZERO).add(transaction.getAmount()));
//            }

            // newer better code implementation of the above written for loop
            Map<String, BigDecimal> transactionMap = transactionList.stream().collect(Collectors.groupingBy(
                    Transaction::getDescription,                                                                            // collects and groups the same descriptions
                    Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))       // maps all the amount together and adds them!
            ));


            return ResponseEntity.ok(transactionMap);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Map<String, BigDecimal>> getUserMonthlySpends(Long userId) {
        userRepository.findById(userId).orElseThrow(
                ()->new NoSuchElementException("User with id: "+userId+" not found"));
        List<Transaction> expense = transactionRepository.findByUserId(userId).stream()
                .filter(x ->
                x.getTransactionType().equals(TransactionType.EXPENSE)).toList();
        if (!expense.isEmpty()){
            Map<String, BigDecimal> transactionMap = expense.stream().collect(Collectors.groupingBy(
                            // here we can't use Transaction like getTransactionSummaryTotal method and have to use tx
                            // because Method references (like Transaction::getDescription) dont work when they are
                            // wrapped inside another function, (YearMonth.from(...)) here!
                    tx -> YearMonth.from(tx.getTransactionDate()).toString(),
                    Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));
            return ResponseEntity.ok(transactionMap);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Map<String, BigDecimal>> getUserTotalSpendsByType(Long userId) {
        userRepository.findById(userId).orElseThrow(
                ()->new NoSuchElementException("User with id: "+userId+" not found"));
        List<Transaction> transactionList = transactionRepository.findByUserId(userId)
                .stream().filter(x->x.getTransactionType().equals(TransactionType.EXPENSE)).toList();
        if (!transactionList.isEmpty()){
            Map<String, BigDecimal> result = transactionList.stream().collect(
                    Collectors.groupingBy(tx ->
                            String.valueOf(tx.getExpenseCategory()),
                            Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                    )
            );
            return ResponseEntity.ok(result);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Map<String, BigDecimal>> getHighestTransactionsByCategory(Long userId) {
        userRepository.findById(userId).orElseThrow(
                ()->new NoSuchElementException("User with id: "+userId+" not found"));
        List<Transaction> transactionList = transactionRepository.findByUserId(userId);
        Map<String, BigDecimal> result = transactionList.stream().collect(Collectors.toMap(tx->
                String.valueOf(tx.getExpenseCategory()),
                Transaction::getAmount,
                BigDecimal::max
        ));
        if (result!=null){
            return ResponseEntity.ok(result);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Map<YearMonth, Map<String, BigDecimal>>> getMonthlyHighestTransactionsByCategory(Long userId) {
        userRepository.findById(userId).orElseThrow(
                ()->new NoSuchElementException("User with id: "+userId+" not found"));
        List<Transaction> transactionList = transactionRepository.findByUserId(userId);
        Map<YearMonth, Map<String, BigDecimal>> resultMap = transactionList.stream().collect(Collectors.groupingBy(tx ->
                        YearMonth.from(tx.getTransactionDate()),
                Collectors.groupingBy(x ->
                                String.valueOf(x.getExpenseCategory()),
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::max))
                )
        ));
        if (!resultMap.isEmpty()){
            return ResponseEntity.ok(resultMap);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Map<String, BigDecimal>> weeklyTransactions(Long userId){
        userRepository.findById(userId).orElseThrow(
                ()->new NoSuchElementException("User with id: "+userId+" not found"));
        List<Transaction> transactionList = transactionRepository.findByUserId(userId).stream().
                filter(tx ->
                        tx.getTransactionType().equals(TransactionType.EXPENSE)).toList();
        if (!transactionList.isEmpty()){
            Map<String, BigDecimal> result = transactionList.stream().collect(Collectors.groupingBy(tx ->
                            getWeek(tx.getTransactionDate()),
                    Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));
            if (!result.isEmpty()){
                return ResponseEntity.ok(result);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private String getWeek(LocalDateTime date){
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        int year = date.get(weekFields.weekBasedYear());
        String weekKey = year + "-W" + weekNumber;
        return weekKey;
    }
}
