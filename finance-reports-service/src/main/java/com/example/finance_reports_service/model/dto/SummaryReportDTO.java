package com.example.finance_reports_service.model.dto;

import java.util.List;

public class SummaryReportDTO {
    private List<MonthlyExpenditureDTO> monthlyExpenditure;
    private List<BudgetAdherenceDTO> budgetAdherence;
    private List<SavingsProgressDTO> savingsProgress;
    private List<CategoryDistributionDTO> categoryDistribution;
    private SavingsForecastDTO savingsForecast;

    // Constructors
    public SummaryReportDTO() {}

    public SummaryReportDTO(List<MonthlyExpenditureDTO> monthlyExpenditure,
                            List<BudgetAdherenceDTO> budgetAdherence,
                            List<SavingsProgressDTO> savingsProgress,
                            List<CategoryDistributionDTO> categoryDistribution,
                            SavingsForecastDTO savingsForecast) {
        this.monthlyExpenditure = monthlyExpenditure;
        this.budgetAdherence = budgetAdherence;
        this.savingsProgress = savingsProgress;
        this.categoryDistribution = categoryDistribution;
        this.savingsForecast = savingsForecast;
    }

    // Getters and Setters
    public List<MonthlyExpenditureDTO> getMonthlyExpenditure() {
        return monthlyExpenditure;
    }

    public void setMonthlyExpenditure(List<MonthlyExpenditureDTO> monthlyExpenditure) {
        this.monthlyExpenditure = monthlyExpenditure;
    }

    public List<BudgetAdherenceDTO> getBudgetAdherence() {
        return budgetAdherence;
    }

    public void setBudgetAdherence(List<BudgetAdherenceDTO> budgetAdherence) {
        this.budgetAdherence = budgetAdherence;
    }

    public List<SavingsProgressDTO> getSavingsProgress() {
        return savingsProgress;
    }

    public void setSavingsProgress(List<SavingsProgressDTO> savingsProgress) {
        this.savingsProgress = savingsProgress;
    }

    public List<CategoryDistributionDTO> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(List<CategoryDistributionDTO> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    public SavingsForecastDTO getSavingsForecast() {
        return savingsForecast;
    }

    public void setSavingsForecast(SavingsForecastDTO savingsForecast) {
        this.savingsForecast = savingsForecast;
    }
}