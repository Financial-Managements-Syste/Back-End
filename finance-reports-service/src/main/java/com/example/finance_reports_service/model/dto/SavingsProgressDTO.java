package com.example.finance_reports_service.model.dto;

import java.time.LocalDate;

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

    // Constructors
    public SavingsProgressDTO() {}

    // Getters and Setters
    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public Double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(Double targetAmount) { this.targetAmount = targetAmount; }

    public Double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(Double currentAmount) { this.currentAmount = currentAmount; }

    public Double getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(Double remainingAmount) { this.remainingAmount = remainingAmount; }

    public Double getPercentageComplete() { return percentageComplete; }
    public void setPercentageComplete(Double percentageComplete) { this.percentageComplete = percentageComplete; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public Integer getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(Integer daysRemaining) { this.daysRemaining = daysRemaining; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getRequiredMonthlySavings() { return requiredMonthlySavings; }
    public void setRequiredMonthlySavings(Double requiredMonthlySavings) { this.requiredMonthlySavings = requiredMonthlySavings; }
}