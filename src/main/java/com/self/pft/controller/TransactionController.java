package com.self.pft.controller;

import com.self.pft.entity.Transaction;
import com.self.pft.entity.request.TransactionRequest;
import com.self.pft.entity.response.TransactionResponse;
import com.self.pft.enums.ExpenseCategory;
import com.self.pft.enums.TransactionType;
import com.self.pft.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

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

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTransactionById(@PathVariable Long id){
        return transactionService.deleteTransactionById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable Long id, @RequestBody TransactionRequest transactionRequest){
        return transactionService.updateTransactionById(id, transactionRequest);
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<Map<String, BigDecimal>> getTransactionsBySummary(@PathVariable Long userId){
        return transactionService.getTransactionSummaryTotal(userId);
    }

    @GetMapping("user/{userId}/monthlySpends")
    public ResponseEntity<Map<String, BigDecimal>> getUserMonthlySpends(@PathVariable Long userId){
        return transactionService.getUserMonthlySpends(userId);
    }

    @GetMapping("user/{userId}/spendsByType")
    public ResponseEntity<Map<String, BigDecimal>> getUserTotalSpendsByType(@PathVariable Long userId){
        return transactionService.getUserTotalSpendsByType(userId);
    }

    @GetMapping("user/{userId}/highest-by-category")
    public ResponseEntity<Map<String, BigDecimal>> getHighestTransactionsByCategory(@PathVariable Long userId){
        return transactionService.getHighestTransactionsByCategory(userId);
    }

    @GetMapping("user/{userId}/monthly-highest-by-category")
    public ResponseEntity<Map<YearMonth, Map<String, BigDecimal>>> getMonthlyHighestTransactionsByCategory(@PathVariable Long userId){
        return transactionService.getMonthlyHighestTransactionsByCategory(userId);
    }

}
