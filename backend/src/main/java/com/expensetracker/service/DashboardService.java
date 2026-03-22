package com.expensetracker.service;

import com.expensetracker.dto.DashboardStatsResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    private String getUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public DashboardStatsResponse getStats(LocalDate startDate, LocalDate endDate) {
        String userId = getUserId();
        
        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        } else {
            // Default to current month
            LocalDate now = LocalDate.now();
            transactions = transactionRepository.findByUserIdAndDateBetween(
                    userId, 
                    now.withDayOfMonth(1), 
                    now.withDayOfMonth(now.lengthOfMonth())
            );
        }

        double totalIncome = 0;
        double totalExpense = 0;
        double totalLent = 0;
        double totalBorrowed = 0;
        Map<String, Double> categoryWiseExpense = new HashMap<>();
        Map<String, Double> monthlyExpenseTrend = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome += t.getAmount();
            } else if (t.getType() == TransactionType.EXPENSE) {
                totalExpense += t.getAmount();
                
                Category cat = categoryRepository.findById(t.getCategoryId()).orElse(null);
                String catName = cat != null ? cat.getName() : "Unknown";
                
                categoryWiseExpense.put(catName, categoryWiseExpense.getOrDefault(catName, 0.0) + t.getAmount());

                String month = t.getDate().getMonth().toString();
                monthlyExpenseTrend.put(month, monthlyExpenseTrend.getOrDefault(month, 0.0) + t.getAmount());
            } else if (t.getType() == TransactionType.LENT) {
                totalLent += t.getAmount();
            } else if (t.getType() == TransactionType.BORROWED) {
                totalBorrowed += t.getAmount();
            }
        }

        return DashboardStatsResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalSavings(totalIncome + totalBorrowed - totalExpense - totalLent)
                .totalLent(totalLent)
                .totalBorrowed(totalBorrowed)
                .categoryWiseExpense(categoryWiseExpense)
                .monthlyExpenseTrend(monthlyExpenseTrend)
                .build();
    }
}
