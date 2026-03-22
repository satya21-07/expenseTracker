package com.expensetracker.repository;

import com.expensetracker.entity.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, String> {
    List<Budget> findByUserId(String userId);
    Optional<Budget> findByUserIdAndCategoryId(String userId, String categoryId);
}
