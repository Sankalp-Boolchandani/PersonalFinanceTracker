package com.self.pft.entity.request;

import com.self.pft.enums.ExpenseCategory;
import com.self.pft.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransactionRequest {

    private Long userId;
    @Positive(message = "Should be greater than 0")
    @NotNull
    private BigDecimal amount;
    @NotBlank(message = "Can't be blank")
    private String description;
    @NotNull(message = "Must be EXPENSE or INCOME")
    private TransactionType transactionType;
    @NotNull(message = "User must provide this date")
    private LocalDateTime transactionDate;
    @NotNull(message = "Can't be empty")
    private ExpenseCategory expenseCategory;
}
