package com.self.pft.service;

import com.self.pft.entity.Transaction;
import com.self.pft.entity.User;
import com.self.pft.entity.request.TransactionRequest;
import com.self.pft.entity.response.TransactionResponse;
import com.self.pft.enums.TransactionType;
import com.self.pft.repository.TransactionRepository;
import com.self.pft.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
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

        Transaction saved = transactionRepository.save(transaction);
        return new ResponseEntity<>(saved, HttpStatus.OK);
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

            transactionRepository.save(transaction);
            return new ResponseEntity<>("Transaction updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Transaction not found with id: "+id, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Map<String, BigDecimal>> getTransactionSummaryTotal(Long userId) {
        userRepository.findById(userId).orElseThrow(
                ()->new NoSuchElementException("User with id: "+userId+" not found"));
        List<Transaction> transactionList = transactionRepository.findByUserId(userId);
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
}
