package com.example.finance_reports_service.model.dto;

public class MonthlyExpenditureDTO {
    private Integer month;
    private Integer year;
    private String categoryName;
    private Double categoryTotal;
    private Integer transactionCount;
    private Double averageTransaction;
    private Double totalIncome;
    private Double totalExpense;
    private Double netSavings;

    // Constructors
    public MonthlyExpenditureDTO() {}

    // Getters and Setters
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getCategoryTotal() { return categoryTotal; }
    public void setCategoryTotal(Double categoryTotal) { this.categoryTotal = categoryTotal; }

    public Integer getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

    public Double getAverageTransaction() { return averageTransaction; }
    public void setAverageTransaction(Double averageTransaction) { this.averageTransaction = averageTransaction; }

    public Double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(Double totalIncome) { this.totalIncome = totalIncome; }

    public Double getTotalExpense() { return totalExpense; }
    public void setTotalExpense(Double totalExpense) { this.totalExpense = totalExpense; }

    public Double getNetSavings() { return netSavings; }
    public void setNetSavings(Double netSavings) { this.netSavings = netSavings; }
}