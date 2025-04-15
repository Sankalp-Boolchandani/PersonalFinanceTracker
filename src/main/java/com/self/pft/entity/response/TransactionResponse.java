package com.self.pft.entity.response;

import com.self.pft.enums.ExpenseCategory;
import com.self.pft.enums.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String description;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ExpenseCategory expenseCategory;

}