package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByUserIdOrPredefinedTrue(String userId);
}
