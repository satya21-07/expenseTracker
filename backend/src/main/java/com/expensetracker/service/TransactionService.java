package com.expensetracker.service;

import com.expensetracker.dto.AlertResponse;
import com.expensetracker.dto.TransactionDto;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;

    private String getUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public Page<TransactionDto> getTransactions(
            String type, String categoryId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        
        String userId = getUserId();
        Page<Transaction> page;

        if (startDate != null && endDate != null) {
            page = transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable);
        } else if (type != null) {
            page = transactionRepository.findByUserIdAndType(userId, TransactionType.valueOf(type.toUpperCase()), pageable);
        } else if (categoryId != null) {
            page = transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable);
        } else {
            page = transactionRepository.findByUserId(userId, pageable);
        }

        return page.map(this::mapToDto);
    }

    public TransactionDto addTransaction(TransactionDto dto) {
        Transaction transaction = Transaction.builder()
                .userId(getUserId())
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .date(dto.getDate())
                .categoryId(dto.getCategoryId())
                .type(dto.getType())
                .build();
        
        Transaction saved = transactionRepository.save(transaction);
        
        // Trigger check budget
        if (saved.getType() == TransactionType.EXPENSE) {
            budgetService.checkBudgetStatus(saved.getCategoryId(), saved.getDate());
        }

        return mapToDto(saved);
    }

    public TransactionDto updateTransaction(String id, TransactionDto dto) {
        Transaction existing = transactionRepository.findById(id).orElseThrow();
        if (!existing.getUserId().equals(getUserId())) throw new RuntimeException("Unauthorized");

        existing.setAmount(dto.getAmount());
        existing.setDescription(dto.getDescription());
        existing.setDate(dto.getDate());
        existing.setCategoryId(dto.getCategoryId());
        existing.setType(dto.getType());

        Transaction saved = transactionRepository.save(existing);
        
        if (saved.getType() == TransactionType.EXPENSE) {
            budgetService.checkBudgetStatus(saved.getCategoryId(), saved.getDate());
        }

        return mapToDto(saved);
    }

    public void deleteTransaction(String id) {
        Transaction existing = transactionRepository.findById(id).orElseThrow();
        if (!existing.getUserId().equals(getUserId())) throw new RuntimeException("Unauthorized");
        transactionRepository.delete(existing);
    }

    private TransactionDto mapToDto(Transaction transaction) {
        Category category = categoryRepository.findById(transaction.getCategoryId()).orElse(null);
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .categoryId(transaction.getCategoryId())
                .categoryName(category != null ? category.getName() : "Unknown")
                .type(transaction.getType())
                .build();
    }
}
