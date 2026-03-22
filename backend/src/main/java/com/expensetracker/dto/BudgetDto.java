package com.expensetracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDto {
    private String id;
    
    @NotNull(message = "CategoryId is required")
    private String categoryId;
    
    private String categoryName;

    @NotNull(message = "Amount is required")
    private Double amount;

    @NotNull(message = "Warning threshold is required")
    private Double warningThreshold;
}
