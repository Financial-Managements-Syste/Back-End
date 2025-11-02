package com.example.finance_reports_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsProgressDTO {
    private Long goalId;
    private String goalName;
    private Double targetAmount;
    private Double currentAmount;
    private Double remainingAmount;
    private Double percentageComplete;
    private LocalDate targetDate;
    private Integer daysRemaining;
    private String status;
    private Double requiredMonthlySavings;
}