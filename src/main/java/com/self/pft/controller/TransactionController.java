package com.self.pft.controller;

import com.self.pft.entity.Transaction;
import com.self.pft.entity.request.TransactionRequest;
import com.self.pft.entity.response.TransactionResponse;
import com.self.pft.enums.TransactionType;
import com.self.pft.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionRequest request){
        ResponseEntity<Transaction> transaction = transactionService.createTransaction(request);
        if (transaction!=null){
            return transaction;
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<TransactionResponse>> getUserTransactionsByUserId(
            @RequestParam Long userId,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
            ){
        return transactionService.getUserTransactionsByUserId(userId, transactionType, startDate, endDate);
    }

}
