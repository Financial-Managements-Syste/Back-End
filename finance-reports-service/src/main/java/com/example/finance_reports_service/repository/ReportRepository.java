// ReportRepository.java - COMPLETE FIXED VERSION
package com.example.finance_reports_service.repository;

import com.example.finance_reports_service.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // REPORT 4: Category-wise Expense Distribution - FIXED with dynamic filtering
    public List<CategoryDistributionDTO> getCategoryDistribution(Long userId,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate,
                                                                 String categoryType) {
        // Default to last 30 days if dates not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "    c.category_name, " +
                        "    c.category_type, " +
                        "    SUM(ABS(t.amount)) as total_amount, " +
                        "    COUNT(*) as transaction_count, " +
                        "    AVG(ABS(t.amount)) as average_amount " +
                        "FROM Transactions t " +
                        "JOIN Categories c ON t.category_id = c.category_id " +
                        "WHERE t.user_id = ? " +
                        "AND t.transaction_date BETWEEN ? AND ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(Date.valueOf(startDate));
        params.add(Date.valueOf(endDate));

        // Add category type filter if specified
        if (categoryType != null && !categoryType.equals("All")) {
            sql.append("AND c.category_type = ? ");
            params.add(categoryType);
        }

        sql.append(
                "GROUP BY c.category_name, c.category_type " +
                        "HAVING SUM(ABS(t.amount)) > 0 " +
                        "ORDER BY total_amount DESC"
        );

        List<CategoryDistributionDTO> results = jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> {
                    CategoryDistributionDTO dto = new CategoryDistributionDTO();
                    dto.setCategoryName(rs.getString("category_name"));
                    dto.setCategoryType(rs.getString("category_type"));
                    dto.setTotalAmount(rs.getDouble("total_amount"));
                    dto.setTransactionCount(rs.getInt("transaction_count"));
                    dto.setAverageAmount(rs.getDouble("average_amount"));
                    dto.setPercentage(0.0);
                    return dto;
                },
                params.toArray()
        );

        // Calculate percentages
        double total = results.stream()
                .mapToDouble(CategoryDistributionDTO::getTotalAmount)
                .sum();

        if (total > 0) {
            results.forEach(dto ->
                    dto.setPercentage((dto.getTotalAmount() / total) * 100)
            );
        }

        return results;
    }

    // REPORT 5: Forecasted Savings Trends - FIXED
    public SavingsForecastDTO getSavingsForecast(Long userId) {
        // First, get monthly aggregates for the last 6 months
        String sql = "SELECT " +
                "    AVG(monthly_income) as avg_income, " +
                "    AVG(monthly_expense) as avg_expense " +
                "FROM ( " +
                "    SELECT " +
                "        EXTRACT(YEAR FROM transaction_date) as year, " +
                "        EXTRACT(MONTH FROM transaction_date) as month, " +
                "        SUM(CASE WHEN transaction_type = 'Income' THEN ABS(amount) ELSE 0 END) as monthly_income, " +
                "        SUM(CASE WHEN transaction_type = 'Expense' THEN ABS(amount) ELSE 0 END) as monthly_expense " +
                "    FROM Transactions " +
                "    WHERE user_id = ? " +
                "        AND transaction_date >= ADD_MONTHS(SYSDATE, -6) " +
                "    GROUP BY EXTRACT(YEAR FROM transaction_date), " +
                "             EXTRACT(MONTH FROM transaction_date) " +
                ")";

        List<SavingsForecastDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            double avgIncome = rs.getDouble("avg_income");
            double avgExpense = rs.getDouble("avg_expense");
            double avgSavings = avgIncome - avgExpense;
            double savingsRate = avgIncome > 0 ? (avgSavings / avgIncome) * 100 : 0;

            String trend;
            if (savingsRate > 20) trend = "POSITIVE";
            else if (savingsRate > 10) trend = "STABLE";
            else trend = "NEGATIVE";

            SavingsForecastDTO dto = new SavingsForecastDTO();
            dto.setAverageIncome(avgIncome);
            dto.setAverageExpense(avgExpense);
            dto.setAverageSavings(avgSavings);
            dto.setSavingsRate(savingsRate);
            dto.setProjected3Months(avgSavings * 3);
            dto.setProjected6Months(avgSavings * 6);
            dto.setProjected12Months(avgSavings * 12);
            dto.setTrend(trend);

            return dto;
        }, userId);

        // Return default values if no results found
        if (results.isEmpty()) {
            SavingsForecastDTO defaultDto = new SavingsForecastDTO();
            defaultDto.setAverageIncome(0.0);
            defaultDto.setAverageExpense(0.0);
            defaultDto.setAverageSavings(0.0);
            defaultDto.setSavingsRate(0.0);
            defaultDto.setProjected3Months(0.0);
            defaultDto.setProjected6Months(0.0);
            defaultDto.setProjected12Months(0.0);
            defaultDto.setTrend("NEGATIVE");
            return defaultDto;
        }

        return results.get(0);
    }

    // REPORT 1: Monthly Expenditure Analysis - FIXED with ABS
    public List<MonthlyExpenditureDTO> getMonthlyExpenditure(Long userId, int month, int year) {
        String sql = "SELECT " +
                "    c.category_name, " +
                "    SUM(ABS(t.amount)) as category_total, " +
                "    COUNT(*) as transaction_count, " +
                "    AVG(ABS(t.amount)) as average_transaction " +
                "FROM Transactions t " +
                "JOIN Categories c ON t.category_id = c.category_id " +
                "WHERE t.user_id = ? " +
                "    AND EXTRACT(MONTH FROM t.transaction_date) = ? " +
                "    AND EXTRACT(YEAR FROM t.transaction_date) = ? " +
                "    AND t.transaction_type = 'Expense' " +
                "GROUP BY c.category_name " +
                "ORDER BY category_total DESC";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    MonthlyExpenditureDTO dto = new MonthlyExpenditureDTO();
                    dto.setMonth(month);
                    dto.setYear(year);
                    dto.setCategoryName(rs.getString("category_name"));
                    dto.setCategoryTotal(rs.getDouble("category_total"));
                    dto.setTransactionCount(rs.getInt("transaction_count"));
                    dto.setAverageTransaction(rs.getDouble("average_transaction"));
                    return dto;
                }, userId, month, year);
    }

    // REPORT 2: Budget Adherence Tracking - FIXED with ABS
    public List<BudgetAdherenceDTO> getBudgetAdherence(Long userId) {
        String sql = "SELECT " +
                "    b.budget_id, " +
                "    b.budget_name, " +
                "    c.category_name, " +
                "    b.budget_amount, " +
                "    NVL(SUM(CASE WHEN t.transaction_type = 'Expense' THEN ABS(t.amount) ELSE 0 END), 0) as spent_amount, " +
                "    b.budget_period " +
                "FROM Budgets b " +
                "JOIN Categories c ON b.category_id = c.category_id " +
                "LEFT JOIN Transactions t ON t.category_id = b.category_id " +
                "    AND t.user_id = b.user_id " +
                "    AND t.transaction_date BETWEEN b.start_date AND b.end_date " +
                "WHERE b.user_id = ? " +
                "    AND b.end_date >= SYSDATE " +
                "GROUP BY b.budget_id, b.budget_name, c.category_name, " +
                "         b.budget_amount, b.budget_period " +
                "ORDER BY b.budget_id";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    double budgetAmount = rs.getDouble("budget_amount");
                    double spentAmount = rs.getDouble("spent_amount");
                    double remainingAmount = budgetAmount - spentAmount;
                    double percentageUsed = budgetAmount > 0 ? (spentAmount / budgetAmount) * 100 : 0;

                    String status;
                    if (spentAmount > budgetAmount) status = "OVER_BUDGET";
                    else if (spentAmount > budgetAmount * 0.9) status = "WARNING";
                    else status = "ON_TRACK";

                    BudgetAdherenceDTO dto = new BudgetAdherenceDTO();
                    dto.setBudgetId(rs.getLong("budget_id"));
                    dto.setBudgetName(rs.getString("budget_name"));
                    dto.setCategoryName(rs.getString("category_name"));
                    dto.setBudgetAmount(budgetAmount);
                    dto.setSpentAmount(spentAmount);
                    dto.setRemainingAmount(remainingAmount);
                    dto.setPercentageUsed(percentageUsed);
                    dto.setStatus(status);
                    dto.setBudgetPeriod(rs.getString("budget_period"));

                    return dto;
                }, userId);
    }

    // REPORT 3: Savings Goal Progress
    public List<SavingsProgressDTO> getSavingsProgress(Long userId) {
        String sql = "SELECT " +
                "    goal_id, " +
                "    goal_name, " +
                "    target_amount, " +
                "    current_amount, " +
                "    target_date, " +
                "    status " +
                "FROM SavingsGoals " +
                "WHERE user_id = ? " +
                "ORDER BY goal_id";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    double targetAmount = rs.getDouble("target_amount");
                    double currentAmount = rs.getDouble("current_amount");
                    double remainingAmount = targetAmount - currentAmount;
                    double percentageComplete = targetAmount > 0 ? (currentAmount / targetAmount) * 100 : 0;

                    Date targetDate = rs.getDate("target_date");
                    int daysRemaining = 0;
                    double requiredMonthlySavings = 0;

                    if (targetDate != null) {
                        LocalDate target = targetDate.toLocalDate();
                        LocalDate now = LocalDate.now();
                        daysRemaining = (int) java.time.temporal.ChronoUnit.DAYS.between(now, target);

                        if (daysRemaining > 0) {
                            double monthsRemaining = daysRemaining / 30.0;
                            if (monthsRemaining > 0) {
                                requiredMonthlySavings = remainingAmount / monthsRemaining;
                            }
                        }
                    }

                    SavingsProgressDTO dto = new SavingsProgressDTO();
                    dto.setGoalId(rs.getLong("goal_id"));
                    dto.setGoalName(rs.getString("goal_name"));
                    dto.setTargetAmount(targetAmount);
                    dto.setCurrentAmount(currentAmount);
                    dto.setRemainingAmount(remainingAmount);
                    dto.setPercentageComplete(percentageComplete);
                    dto.setTargetDate(targetDate != null ? targetDate.toLocalDate() : null);
                    dto.setDaysRemaining(daysRemaining);
                    dto.setStatus(rs.getString("status"));
                    dto.setRequiredMonthlySavings(requiredMonthlySavings);

                    return dto;
                }, userId);
    }

    // REPORT 6: Complete Summary Report
    public SummaryReportDTO getSummaryReport(Long userId) {
        SummaryReportDTO summary = new SummaryReportDTO();

        // Get current month and year
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // Populate all report data
        summary.setMonthlyExpenditure(getMonthlyExpenditure(userId, currentMonth, currentYear));
        summary.setBudgetAdherence(getBudgetAdherence(userId));
        summary.setSavingsProgress(getSavingsProgress(userId));
        summary.setCategoryDistribution(getCategoryDistribution(userId, now.minusDays(30), now, "All"));
        summary.setSavingsForecast(getSavingsForecast(userId));

        return summary;
    }
}