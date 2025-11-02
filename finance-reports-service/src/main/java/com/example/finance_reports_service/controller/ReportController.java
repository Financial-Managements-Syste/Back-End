// ReportController.java
package com.example.finance_reports_service.controller;

import com.example.finance_reports_service.model.dto.*;
import com.example.finance_reports_service.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Main reports page
    @GetMapping
    public String reportsHome() {
        return "reports";
    }

    // Report 1: Monthly Expenditure
    @GetMapping("/monthly-expenditure")
    public String monthlyExpenditure(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model) {

        if (month == null) month = LocalDate.now().getMonthValue();
        if (year == null) year = LocalDate.now().getYear();

        List<MonthlyExpenditureDTO> data = reportService.getMonthlyExpenditure(userId, month, year);

        model.addAttribute("userId", userId);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("expenditures", data);

        return "monthly-expenditure";
    }

    // Report 2: Budget Adherence
    @GetMapping("/budget-adherence")
    public String budgetAdherence(
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        List<BudgetAdherenceDTO> data = reportService.getBudgetAdherence(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("budgets", data);

        return "budget-adherence";
    }

    // Report 3: Savings Progress
    @GetMapping("/savings-progress")
    public String savingsProgress(
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        List<SavingsProgressDTO> data = reportService.getSavingsProgress(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("goals", data);

        return "savings-progress";
    }

    // Report 4: Category Distribution
    @GetMapping("/category-distribution")
    public String categoryDistribution(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        if (startDate == null) startDate = LocalDate.now().withDayOfMonth(1);
        if (endDate == null) endDate = LocalDate.now();

        List<CategoryDistributionDTO> data = reportService.getCategoryDistribution(userId, startDate, endDate);

        model.addAttribute("userId", userId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("categories", data);

        return "category-distribution";
    }

    // Report 5: Savings Forecast
    @GetMapping("/savings-forecast")
    public String savingsForecast(
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        SavingsForecastDTO data = reportService.getSavingsForecast(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("forecast", data);

        return "savings-forecast";
    }

    // REST API Endpoints (for AJAX/JSON responses)

    @GetMapping("/api/monthly-expenditure")
    @ResponseBody
    public List<MonthlyExpenditureDTO> getMonthlyExpenditureAPI(
            @RequestParam Long userId,
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.getMonthlyExpenditure(userId, month, year);
    }

    @GetMapping("/api/budget-adherence")
    @ResponseBody
    public List<BudgetAdherenceDTO> getBudgetAdherenceAPI(@RequestParam Long userId) {
        return reportService.getBudgetAdherence(userId);
    }

    @GetMapping("/api/savings-progress")
    @ResponseBody
    public List<SavingsProgressDTO> getSavingsProgressAPI(@RequestParam Long userId) {
        return reportService.getSavingsProgress(userId);
    }

    @GetMapping("/api/category-distribution")
    @ResponseBody
    public List<CategoryDistributionDTO> getCategoryDistributionAPI(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getCategoryDistribution(userId, startDate, endDate);
    }

    @GetMapping("/api/savings-forecast")
    @ResponseBody
    public SavingsForecastDTO getSavingsForecastAPI(@RequestParam Long userId) {
        return reportService.getSavingsForecast(userId);
    }
}