package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {
    private Double totalIncome;
    private Double totalExpense;
    private Double totalSavings;
    private Double totalLent;
    private Double totalBorrowed;
    private Map<String, Double> monthlyExpenseTrend;
    private Map<String, Double> categoryWiseExpense;
}
