package com.self.pft.entity.request;

import com.self.pft.enums.TransactionType;
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
    private BigDecimal amount;
    private String description;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
}
