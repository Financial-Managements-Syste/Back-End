package com.example.finance_reports_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAdherenceDTO {
    private Long budgetId;
    private String budgetName;
    private String categoryName;
    private Double budgetAmount;
    private Double spentAmount;
    private Double remainingAmount;
    private Double percentageUsed;
    private String status; // ON_TRACK, WARNING, OVER_BUDGET
    private String budgetPeriod;
}