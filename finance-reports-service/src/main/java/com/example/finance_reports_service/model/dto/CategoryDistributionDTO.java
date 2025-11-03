package com.example.finance_reports_service.model.dto;

public class CategoryDistributionDTO {
    private String categoryName;
    private String categoryType;
    private Double totalAmount;
    private Integer transactionCount;
    private Double percentage;
    private Double averageAmount;

    // Constructors
    public CategoryDistributionDTO() {}

    public CategoryDistributionDTO(String categoryName, String categoryType, Double totalAmount,
                                   Integer transactionCount, Double percentage, Double averageAmount) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
        this.percentage = percentage;
        this.averageAmount = averageAmount;
    }

    // Getters and Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryType() { return categoryType; }
    public void setCategoryType(String categoryType) { this.categoryType = categoryType; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Integer getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public Double getAverageAmount() { return averageAmount; }
    public void setAverageAmount(Double averageAmount) { this.averageAmount = averageAmount; }
}

