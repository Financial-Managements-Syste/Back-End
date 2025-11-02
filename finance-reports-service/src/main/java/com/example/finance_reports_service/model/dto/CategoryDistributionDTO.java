package com.example.finance_reports_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDistributionDTO {
    private String categoryName;
    private String categoryType;
    private Double totalAmount;
    private Integer transactionCount;
    private Double percentage;
    private Double averageAmount;
}