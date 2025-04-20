package com.self.pft.controller;

import com.self.pft.entity.Transaction;
import com.self.pft.entity.request.TransactionRequest;
import com.self.pft.entity.response.TransactionResponse;
import com.self.pft.enums.TransactionType;
import com.self.pft.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction APIs")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Operation(summary = "add-transaction")
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals(request.getUsername())){
            ResponseEntity<Transaction> transaction = transactionService.createTransaction(request);
            if (transaction!=null){
                return transaction;
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Operation(summary = "get-all-transactions")
    @GetMapping("/user")
    public ResponseEntity<List<TransactionResponse>> getUserTransactionsByUserId(
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
            ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return transactionService.getUserTransactionsByUserId(authentication.getName(), transactionType, startDate, endDate);
    }

    @Operation(summary = "delete-transaction")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTransactionById(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return transactionService.deleteTransactionById(authentication.getName(), id);
    }

    @Operation(summary = "update-transaction")
    @PutMapping("{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable Long id, @RequestBody TransactionRequest transactionRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return transactionService.updateTransactionById(authentication.getName(), id, transactionRequest);
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

    @GetMapping("user/{userId}/weekly-total")
    public ResponseEntity<Map<String, BigDecimal>> getWeeklyTotal(@PathVariable Long userId){
        return transactionService.weeklyTransactions(userId);
    }

    @GetMapping("report")
    public ResponseEntity<Map<YearMonth, Map<TransactionType, BigDecimal>>> getReport(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return transactionService.getReport(authentication.getName());
    }

}
