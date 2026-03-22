package com.expensetracker.controller;

import com.expensetracker.dto.AlertResponse;
import com.expensetracker.dto.BudgetDto;
import com.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService service;

    @GetMapping
    public ResponseEntity<List<BudgetDto>> getBudgets() {
        return ResponseEntity.ok(service.getAllBudgets());
    }

    @PostMapping
    public ResponseEntity<BudgetDto> createOrUpdateBudget(@Valid @RequestBody BudgetDto dto) {
        return ResponseEntity.ok(service.createOrUpdateBudget(dto));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertResponse>> getAlerts() {
        return ResponseEntity.ok(service.getAlerts());
    }
}
