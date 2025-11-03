package com.example.finance_reports_service.model.dto;

public class SavingsForecastDTO {
    private Double averageIncome;
    private Double averageExpense;
    private Double averageSavings;
    private Double savingsRate;
    private Double projected3Months;
    private Double projected6Months;
    private Double projected12Months;
    private String trend;

    // Constructors
    public SavingsForecastDTO() {}

    public SavingsForecastDTO(Double averageIncome, Double averageExpense, Double averageSavings,
                              Double savingsRate, Double projected3Months, Double projected6Months,
                              Double projected12Months, String trend) {
        this.averageIncome = averageIncome;
        this.averageExpense = averageExpense;
        this.averageSavings = averageSavings;
        this.savingsRate = savingsRate;
        this.projected3Months = projected3Months;
        this.projected6Months = projected6Months;
        this.projected12Months = projected12Months;
        this.trend = trend;
    }

    // Getters and Setters
    public Double getAverageIncome() { return averageIncome; }
    public void setAverageIncome(Double averageIncome) { this.averageIncome = averageIncome; }

    public Double getAverageExpense() { return averageExpense; }
    public void setAverageExpense(Double averageExpense) { this.averageExpense = averageExpense; }

    public Double getAverageSavings() { return averageSavings; }
    public void setAverageSavings(Double averageSavings) { this.averageSavings = averageSavings; }

    public Double getSavingsRate() { return savingsRate; }
    public void setSavingsRate(Double savingsRate) { this.savingsRate = savingsRate; }

    public Double getProjected3Months() { return projected3Months; }
    public void setProjected3Months(Double projected3Months) { this.projected3Months = projected3Months; }

    public Double getProjected6Months() { return projected6Months; }
    public void setProjected6Months(Double projected6Months) { this.projected6Months = projected6Months; }

    public Double getProjected12Months() { return projected12Months; }
    public void setProjected12Months(Double projected12Months) { this.projected12Months = projected12Months; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
}