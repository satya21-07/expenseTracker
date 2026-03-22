package com.expensetracker.service;

import com.expensetracker.dto.AlertResponse;
import com.expensetracker.dto.BudgetDto;
import com.expensetracker.entity.Alert;
import com.expensetracker.entity.Budget;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.repository.AlertRepository;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final AlertRepository alertRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    private String getUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public List<BudgetDto> getAllBudgets() {
        return budgetRepository.findByUserId(getUserId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BudgetDto createOrUpdateBudget(BudgetDto dto) {
        String userId = getUserId();
        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryId(userId, dto.getCategoryId());
        
        Budget budget = existing.orElse(new Budget());
        budget.setUserId(userId);
        budget.setCategoryId(dto.getCategoryId());
        budget.setAmount(dto.getAmount());
        budget.setWarningThreshold(dto.getWarningThreshold());
        
        return mapToDto(budgetRepository.save(budget));
    }

    public AlertResponse checkBudgetStatus(String categoryId, LocalDate date) {
        String userId = getUserId();
        Optional<Budget> optBudget = budgetRepository.findByUserIdAndCategoryId(userId, categoryId);
        
        if (optBudget.isEmpty()) return null;
        
        Budget budget = optBudget.get();
        
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        
        List<Transaction> expenses = transactionRepository.findMonthlyExpensesByCategory(
                userId, categoryId, startOfMonth, endOfMonth);
                
        double totalSpent = expenses.stream().mapToDouble(Transaction::getAmount).sum();
        double percentage = (totalSpent / budget.getAmount()) * 100;
        
        String status = "SAFE";
        if (percentage >= 100) {
            status = "EXCEEDED";
        } else if (percentage >= budget.getWarningThreshold()) {
            status = "WARNING";
        }
        
        String monthYear = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        
        // Save alert if WARNING or EXCEEDED
        if (!status.equals("SAFE")) {
            Optional<Alert> existingAlert = alertRepository.findByUserIdAndCategoryIdAndMonthYear(userId, categoryId, monthYear);
            Alert alert = existingAlert.orElse(new Alert());
            alert.setUserId(userId);
            alert.setCategoryId(categoryId);
            alert.setMonthYear(monthYear);
            alert.setMaxLimit(budget.getAmount());
            alert.setSpent(totalSpent);
            alert.setPercentageUsed(percentage);
            alert.setStatus(status);
            alertRepository.save(alert);
        }
        
        Category category = categoryRepository.findById(categoryId).orElse(null);
        String categoryName = category != null ? category.getName() : "Unknown";

        return AlertResponse.builder()
                .category(categoryName)
                .maxLimit(budget.getAmount())
                .spent(totalSpent)
                .percentageUsed(percentage)
                .status(status)
                .build();
    }

    public List<AlertResponse> getAlerts() {
        return alertRepository.findByUserId(getUserId())
                .stream()
                .map(alert -> {
                    Category cat = categoryRepository.findById(alert.getCategoryId()).orElse(null);
                    return AlertResponse.builder()
                            .category(cat != null ? cat.getName() : "Unknown")
                            .maxLimit(alert.getMaxLimit())
                            .spent(alert.getSpent())
                            .percentageUsed(alert.getPercentageUsed())
                            .status(alert.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private BudgetDto mapToDto(Budget budget) {
        Category category = categoryRepository.findById(budget.getCategoryId()).orElse(null);
        return BudgetDto.builder()
                .id(budget.getId())
                .categoryId(budget.getCategoryId())
                .categoryName(category != null ? category.getName() : "Unknown")
                .amount(budget.getAmount())
                .warningThreshold(budget.getWarningThreshold())
                .build();
    }
}
