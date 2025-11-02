package com.example.finance_reports_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsForecastDTO {
    private Double averageMonthlyIncome;
    private Double averageMonthlyExpense;
    private Double averageMonthlySavings;
    private Double savingsRate;
    private Double forecast3Months;
    private Double forecast6Months;
    private Double forecast12Months;
    private String trend; // POSITIVE, STABLE, NEGATIVE
}