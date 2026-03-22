package com.expensetracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alerts")
public class Alert {

    @Id
    private String id;
    
    private String categoryId;
    private Double maxLimit;
    private Double spent;
    private Double percentageUsed;
    private String status; // SAFE, WARNING, EXCEEDED
    private String monthYear; // e.g., "2024-03"
    private String userId;
}
