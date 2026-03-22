//class is used to automatically insert default data into the database when your Spring Boot application starts

package com.expensetracker.config;

import com.expensetracker.entity.Category;
import com.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration //This class contains configuration beans. Please load them into the IoC container.
@RequiredArgsConstructor //It automatically creates a constructor for all final fields.
public class DataInitializer implements CommandLineRunner {
        //CommandLineRunner is a Spring Boot interface."Run this code automatically when the application starts"
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            List<Category> predefinedCategories = List.of(
                    Category.builder().name("Food & Dining").color("#FF5733").predefined(true).build(),
                    Category.builder().name("Transport").color("#3380FF").predefined(true).build(),
                    Category.builder().name("Utilities").color("#33FF57").predefined(true).build(),
                    Category.builder().name("Entertainment").color("#E733FF").predefined(true).build(),
                    Category.builder().name("Salary").color("#2ECC71").predefined(true).build(),
                    Category.builder().name("Other").color("#7F8C8D").predefined(true).build()
            );
            categoryRepository.saveAll(predefinedCategories);
        }
    }
}
