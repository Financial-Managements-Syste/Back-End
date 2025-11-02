// ReportRepository.java
package com.example.finance_reports_service.repository;

import com.example.finance_reports_service.model.dto.*;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.CallableStatement;
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

    // REPORT 1: Monthly Expenditure Analysis
    public List<MonthlyExpenditureDTO> getMonthlyExpenditure(Long userId, int month, int year) {
        String sql = "SELECT " +
                "    EXTRACT(MONTH FROM t.transaction_date) as month, " +
                "    EXTRACT(YEAR FROM t.transaction_date) as year, " +
                "    SUM(CASE WHEN t.transaction_type = 'Income' THEN t.amount ELSE 0 END) as total_income, " +
                "    SUM(CASE WHEN t.transaction_type = 'Expense' THEN t.amount ELSE 0 END) as total_expense, " +
                "    SUM(CASE WHEN t.transaction_type = 'Income' THEN t.amount ELSE -t.amount END) as net_savings, " +
                "    COUNT(*) as transaction_count, " +
                "    AVG(t.amount) as average_transaction, " +
                "    c.category_name, " +
                "    SUM(t.amount) as category_total " +
                "FROM Transactions t " +
                "JOIN Categories c ON t.category_id = c.category_id " +
                "WHERE t.user_id = ? " +
                "    AND EXTRACT(MONTH FROM t.transaction_date) = ? " +
                "    AND EXTRACT(YEAR FROM t.transaction_date) = ? " +
                "GROUP BY EXTRACT(MONTH FROM t.transaction_date), " +
                "         EXTRACT(YEAR FROM t.transaction_date), " +
                "         c.category_name " +
                "ORDER BY category_total DESC";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new MonthlyExpenditureDTO(
                        rs.getInt("month"),
                        rs.getInt("year"),
                        rs.getDouble("total_income"),
                        rs.getDouble("total_expense"),
                        rs.getDouble("net_savings"),
                        rs.getInt("transaction_count"),
                        rs.getDouble("average_transaction"),
                        rs.getString("category_name"),
                        rs.getDouble("category_total")
                ), userId, month, year);
    }

    // REPORT 2: Budget Adherence Tracking
    public List<BudgetAdherenceDTO> getBudgetAdherence(Long userId) {
        String sql = "SELECT " +
                "    b.budget_id, " +
                "    b.budget_name, " +
                "    c.category_name, " +
                "    b.budget_amount, " +
                "    NVL(SUM(t.amount), 0) as spent_amount, " +
                "    b.budget_amount - NVL(SUM(t.amount), 0) as remaining_amount, " +
                "    ROUND((NVL(SUM(t.amount), 0) / b.budget_amount) * 100, 2) as percentage_used, " +
                "    CASE " +
                "        WHEN NVL(SUM(t.amount), 0) > b.budget_amount THEN 'OVER_BUDGET' " +
                "        WHEN NVL(SUM(t.amount), 0) > b.budget_amount * 0.9 THEN 'WARNING' " +
                "        ELSE 'ON_TRACK' " +
                "    END as status, " +
                "    b.budget_period " +
                "FROM Budgets b " +
                "JOIN Categories c ON b.category_id = c.category_id " +
                "LEFT JOIN Transactions t ON t.category_id = b.category_id " +
                "    AND t.user_id = b.user_id " +
                "    AND t.transaction_date BETWEEN b.start_date AND b.end_date " +
                "    AND t.transaction_type = 'Expense' " +
                "WHERE b.user_id = ? " +
                "    AND b.end_date >= SYSDATE " +
                "GROUP BY b.budget_id, b.budget_name, c.category_name, " +
                "         b.budget_amount, b.budget_period " +
                "ORDER BY percentage_used DESC";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new BudgetAdherenceDTO(
                        rs.getLong("budget_id"),
                        rs.getString("budget_name"),
                        rs.getString("category_name"),
                        rs.getDouble("budget_amount"),
                        rs.getDouble("spent_amount"),
                        rs.getDouble("remaining_amount"),
                        rs.getDouble("percentage_used"),
                        rs.getString("status"),
                        rs.getString("budget_period")
                ), userId);
    }

    // REPORT 3: Savings Goal Progress
    public List<SavingsProgressDTO> getSavingsProgress(Long userId) {
        String sql = "SELECT " +
                "    goal_id, " +
                "    goal_name, " +
                "    target_amount, " +
                "    current_amount, " +
                "    target_amount - current_amount as remaining_amount, " +
                "    ROUND((current_amount / target_amount) * 100, 2) as percentage_complete, " +
                "    target_date, " +
                "    TRUNC(target_date - SYSDATE) as days_remaining, " +
                "    status, " +
                "    CASE " +
                "        WHEN TRUNC(target_date - SYSDATE) > 0 THEN " +
                "            ROUND((target_amount - current_amount) / " +
                "            (TRUNC(target_date - SYSDATE) / 30), 2) " +
                "        ELSE 0 " +
                "    END as required_monthly_savings " +
                "FROM SavingsGoals " +
                "WHERE user_id = ? " +
                "ORDER BY percentage_complete DESC";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new SavingsProgressDTO(
                        rs.getLong("goal_id"),
                        rs.getString("goal_name"),
                        rs.getDouble("target_amount"),
                        rs.getDouble("current_amount"),
                        rs.getDouble("remaining_amount"),
                        rs.getDouble("percentage_complete"),
                        rs.getDate("target_date").toLocalDate(),
                        rs.getInt("days_remaining"),
                        rs.getString("status"),
                        rs.getDouble("required_monthly_savings")
                ), userId);
    }

    // REPORT 4: Category-wise Expense Distribution
    public List<CategoryDistributionDTO> getCategoryDistribution(Long userId,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate) {
        String sql = "SELECT " +
                "    c.category_name, " +
                "    c.category_type, " +
                "    SUM(t.amount) as total_amount, " +
                "    COUNT(*) as transaction_count, " +
                "    ROUND((SUM(t.amount) / " +
                "        (SELECT SUM(amount) FROM Transactions " +
                "         WHERE user_id = ? " +
                "         AND transaction_date BETWEEN ? AND ?)) * 100, 2) as percentage, " +
                "    AVG(t.amount) as average_amount " +
                "FROM Transactions t " +
                "JOIN Categories c ON t.category_id = c.category_id " +
                "WHERE t.user_id = ? " +
                "    AND t.transaction_date BETWEEN ? AND ? " +
                "GROUP BY c.category_name, c.category_type " +
                "HAVING SUM(t.amount) > 0 " +
                "ORDER BY total_amount DESC";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new CategoryDistributionDTO(
                        rs.getString("category_name"),
                        rs.getString("category_type"),
                        rs.getDouble("total_amount"),
                        rs.getInt("transaction_count"),
                        rs.getDouble("percentage"),
                        rs.getDouble("average_amount")
                ), userId, Date.valueOf(startDate), Date.valueOf(endDate),
                userId, Date.valueOf(startDate), Date.valueOf(endDate));
    }

    // REPORT 5: Forecasted Savings Trends
    public SavingsForecastDTO getSavingsForecast(Long userId) {
        String sql = "SELECT " +
                "    AVG(CASE WHEN transaction_type = 'Income' THEN amount ELSE 0 END) as avg_income, " +
                "    AVG(CASE WHEN transaction_type = 'Expense' THEN amount ELSE 0 END) as avg_expense, " +
                "    AVG(CASE WHEN transaction_type = 'Income' THEN amount ELSE -amount END) as avg_savings " +
                "FROM ( " +
                "    SELECT " +
                "        EXTRACT(YEAR FROM transaction_date) as year, " +
                "        EXTRACT(MONTH FROM transaction_date) as month, " +
                "        transaction_type, " +
                "        SUM(amount) as amount " +
                "    FROM Transactions " +
                "    WHERE user_id = ? " +
                "        AND transaction_date >= ADD_MONTHS(SYSDATE, -6) " +
                "    GROUP BY EXTRACT(YEAR FROM transaction_date), " +
                "             EXTRACT(MONTH FROM transaction_date), " +
                "             transaction_type " +
                ")";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            double avgIncome = rs.getDouble("avg_income");
            double avgExpense = rs.getDouble("avg_expense");
            double avgSavings = avgIncome - avgExpense;
            double savingsRate = avgIncome > 0 ? (avgSavings / avgIncome) * 100 : 0;

            String trend;
            if (savingsRate > 20) trend = "POSITIVE";
            else if (savingsRate > 10) trend = "STABLE";
            else trend = "NEGATIVE";

            return new SavingsForecastDTO(
                    avgIncome,
                    avgExpense,
                    avgSavings,
                    savingsRate,
                    avgSavings * 3,
                    avgSavings * 6,
                    avgSavings * 12,
                    trend
            );
        }, userId);
    }
}