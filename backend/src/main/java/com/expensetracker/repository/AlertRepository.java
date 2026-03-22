package com.expensetracker.repository;

import com.expensetracker.entity.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert> findByUserId(String userId);
    Optional<Alert> findByUserIdAndCategoryIdAndMonthYear(String userId, String categoryId, String monthYear);
}
