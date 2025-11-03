package com.example.finance_reports_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDTO {
    private List<MonthlyExpenditureDTO> monthlyExpenditure;
    private List<BudgetAdherenceDTO> budgetAdherence;
    private List<SavingsProgressDTO> savingsProgress;
    private List<CategoryDistributionDTO> categoryDistribution;
    private SavingsForecastDTO savingsForecast;
}