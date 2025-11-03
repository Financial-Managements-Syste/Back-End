// ReportController.java
package com.example.finance_reports_service.controller;

import com.example.finance_reports_service.model.dto.*;
import com.example.finance_reports_service.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * REPORT 1: Monthly Expenditure Analysis
     * GET /api/reports/monthly-expenditure?userId=1&month=10&year=2025
     */
    @GetMapping("/monthly-expenditure")
    public ResponseEntity<List<MonthlyExpenditureDTO>> getMonthlyExpenditure(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        if (month == null) month = LocalDate.now().getMonthValue();
        if (year == null) year = LocalDate.now().getYear();

        List<MonthlyExpenditureDTO> data = reportService.getMonthlyExpenditure(userId, month, year);
        return ResponseEntity.ok(data);
    }

    /**
     * REPORT 2: Budget Adherence Tracking
     * GET /api/reports/budget-adherence?userId=1
     */
    @GetMapping("/budget-adherence")
    public ResponseEntity<List<BudgetAdherenceDTO>> getBudgetAdherence(
            @RequestParam Long userId) {

        List<BudgetAdherenceDTO> data = reportService.getBudgetAdherence(userId);
        return ResponseEntity.ok(data);
    }

    /**
     * REPORT 3: Savings Goal Progress
     * GET /api/reports/savings-progress?userId=1
     */
    @GetMapping("/savings-progress")
    public ResponseEntity<List<SavingsProgressDTO>> getSavingsProgress(
            @RequestParam Long userId) {

        List<SavingsProgressDTO> data = reportService.getSavingsProgress(userId);
        return ResponseEntity.ok(data);
    }

    /**
     * REPORT 4: Category-wise Expense Distribution
     * GET /api/reports/category-distribution?userId=1&startDate=2025-10-01&endDate=2025-10-31
     */
    @GetMapping("/category-distribution")
    public ResponseEntity<List<CategoryDistributionDTO>> getCategoryDistribution(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().withDayOfMonth(1);
        if (endDate == null) endDate = LocalDate.now();

        List<CategoryDistributionDTO> data = reportService.getCategoryDistribution(userId, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    /**
     * REPORT 5: Forecasted Savings Trends
     * GET /api/reports/savings-forecast?userId=1
     */
    @GetMapping("/savings-forecast")
    public ResponseEntity<SavingsForecastDTO> getSavingsForecast(
            @RequestParam Long userId) {

        SavingsForecastDTO data = reportService.getSavingsForecast(userId);
        return ResponseEntity.ok(data);
    }


    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryDTO> getReportsSummary(@RequestParam Long userId) {
        ReportSummaryDTO summary = new ReportSummaryDTO();

        LocalDate now = LocalDate.now();
        summary.setMonthlyExpenditure(reportService.getMonthlyExpenditure(userId, now.getMonthValue(), now.getYear()));
        summary.setBudgetAdherence(reportService.getBudgetAdherence(userId));
        summary.setSavingsProgress(reportService.getSavingsProgress(userId));
        summary.setCategoryDistribution(reportService.getCategoryDistribution(userId, now.withDayOfMonth(1), now));
        summary.setSavingsForecast(reportService.getSavingsForecast(userId));

        return ResponseEntity.ok(summary);
    }
}