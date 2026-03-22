package com.expensetracker.dto;

import com.expensetracker.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String id;

    @NotNull(message = "Amount is required")
    private Double amount;
    
    private String description;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "CategoryId is required")
    private String categoryId;

    private String categoryName;

    @NotNull(message = "Type is required")
    private TransactionType type;
}
