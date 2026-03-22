package com.expensetracker.repository;

import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Page<Transaction> findByUserId(String userId, Pageable pageable);

    Page<Transaction> findByUserIdAndType(String userId, TransactionType type, Pageable pageable);
    
    Page<Transaction> findByUserIdAndCategoryId(String userId, String categoryId, Pageable pageable);

    Page<Transaction> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("{ 'userId': ?0, 'categoryId': ?1, 'type': 'EXPENSE', 'date': { $gte: ?2, $lte: ?3 } }")
    List<Transaction> findMonthlyExpensesByCategory(String userId, String categoryId, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);
}
