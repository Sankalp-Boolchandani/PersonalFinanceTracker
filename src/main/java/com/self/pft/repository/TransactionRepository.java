package com.self.pft.repository;

import com.self.pft.entity.Transaction;
import com.self.pft.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    // task 4: finding transactions with different filters
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId "
            + "AND (:transactionType IS NULL OR t.transactionType = :transactionType) "
            + "AND (:startDate IS NULL OR t.transactionDate >= :startDate) "
            + "AND (:endDate IS NULL OR t.transactionDate <= :endDate)")
    List<Transaction> findTransactionsByUserWithFilters(
            @Param("userId") Long userId,
            @Param("transactionType") TransactionType transactionType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
