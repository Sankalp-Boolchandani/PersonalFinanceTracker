package com.self.pft.service;

import com.self.pft.entity.Transaction;
import com.self.pft.entity.User;
import com.self.pft.entity.request.TransactionRequest;
import com.self.pft.entity.response.TransactionResponse;
import com.self.pft.enums.TransactionType;
import com.self.pft.repository.TransactionRepository;
import com.self.pft.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
}
