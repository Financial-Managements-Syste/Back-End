package com.example.finance_reports_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyExpenditureDTO {
    private int month;
    private int year;
    private Double totalIncome;
    private Double totalExpense;
    private Double netSavings;
    private Integer transactionCount;
    private Double averageTransaction;
    private String categoryName;
    private Double categoryTotal;
}