package com.example.finance_reports_service.service;

import com.example.finance_reports_service.model.dto.*;
import com.example.finance_reports_service.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    /**
     * REPORT 1: Monthly Expenditure Analysis
     */
    public List<MonthlyExpenditureDTO> getMonthlyExpenditure(Long userId, int month, int year) {
        return reportRepository.getMonthlyExpenditure(userId, month, year);
    }

    /**
     * REPORT 2: Budget Adherence Tracking
     */
    public List<BudgetAdherenceDTO> getBudgetAdherence(Long userId) {
        return reportRepository.getBudgetAdherence(userId);
    }

    /**
     * REPORT 3: Savings Goal Progress
     */
    public List<SavingsProgressDTO> getSavingsProgress(Long userId) {
        return reportRepository.getSavingsProgress(userId);
    }

    /**
     * REPORT 4: Category-wise Expense Distribution
     * Supports categoryType filter (Expense / Income / All)
     */
    public List<CategoryDistributionDTO> getCategoryDistribution(Long userId,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate,
                                                                 String categoryType) {
        return reportRepository.getCategoryDistribution(userId, startDate, endDate, categoryType);
    }

    /**
     * REPORT 5: Forecasted Savings Trends
     */
    public SavingsForecastDTO getSavingsForecast(Long userId) {
        return reportRepository.getSavingsForecast(userId);
    }

    /**
     * REPORT 6: Complete Summary Report
     */
    public SummaryReportDTO getSummaryReport(Long userId) {
        return reportRepository.getSummaryReport(userId);
    }
}
