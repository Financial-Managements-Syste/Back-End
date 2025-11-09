// ReportController.java - COMPLETE FIXED VERSION
package com.example.finance_reports_service.controller;

import com.example.finance_reports_service.model.dto.*;
import com.example.finance_reports_service.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    /**
     * REPORT 1: Monthly Expenditure Analysis
     * GET /api/reports/monthly-expenditure?userId=1&month=11&year=2025
     */
    @GetMapping("/monthly-expenditure")
    public ResponseEntity<List<MonthlyExpenditureDTO>> getMonthlyExpenditure(
            @RequestParam Long userId,
            @RequestParam int month,
            @RequestParam int year) {

        List<MonthlyExpenditureDTO> result = reportRepository.getMonthlyExpenditure(userId, month, year);
        return ResponseEntity.ok(result);
    }

    /**
     * REPORT 2: Budget Adherence Tracking
     * GET /api/reports/budget-adherence?userId=1
     */
    @GetMapping("/budget-adherence")
    public ResponseEntity<List<BudgetAdherenceDTO>> getBudgetAdherence(
            @RequestParam Long userId) {

        List<BudgetAdherenceDTO> result = reportRepository.getBudgetAdherence(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * REPORT 3: Savings Goal Progress
     * GET /api/reports/savings-progress?userId=1
     */
    @GetMapping("/savings-progress")
    public ResponseEntity<List<SavingsProgressDTO>> getSavingsProgress(
            @RequestParam Long userId) {

        List<SavingsProgressDTO> result = reportRepository.getSavingsProgress(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * REPORT 4: Category-wise Expense Distribution - FIXED
     * GET /api/reports/category-distribution?userId=1&startDate=2025-01-01&endDate=2025-11-09&categoryType=Expense
     */
    @GetMapping("/category-distribution")
    public ResponseEntity<List<CategoryDistributionDTO>> getCategoryDistribution(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "All") String categoryType) {

        // If dates not provided, use defaults (handled in repository)
        List<CategoryDistributionDTO> result = reportRepository.getCategoryDistribution(
                userId, startDate, endDate, categoryType);
        return ResponseEntity.ok(result);
    }

    /**
     * REPORT 5: Forecasted Savings Trends
     * GET /api/reports/savings-forecast?userId=1
     */
    @GetMapping("/savings-forecast")
    public ResponseEntity<SavingsForecastDTO> getSavingsForecast(
            @RequestParam Long userId) {

        SavingsForecastDTO result = reportRepository.getSavingsForecast(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * REPORT 6: Complete Summary Report
     * GET /api/reports/summary?userId=1
     */
    @GetMapping("/summary")
    public ResponseEntity<SummaryReportDTO> getSummaryReport(
            @RequestParam Long userId) {

        SummaryReportDTO result = reportRepository.getSummaryReport(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Reports Service is running");
    }
}