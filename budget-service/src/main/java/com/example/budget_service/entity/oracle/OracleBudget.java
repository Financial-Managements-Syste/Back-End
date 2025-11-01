package com.example.budget_service.entity.oracle;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Budgets")
public class OracleBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long budgetId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "budget_name")
    private String budgetName;

    @Column(name = "budget_description")
    private String budgetDescription;

    @Column(name = "budget_amount", nullable = false)
    private Double budgetAmount;

    @Column(name = "budget_period", nullable = false)
    private String budgetPeriod;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Getters & Setters ---
    public Long getBudgetId() { return budgetId; }
    public void setBudgetId(Long budgetId) { this.budgetId = budgetId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getBudgetName() { return budgetName; }
    public void setBudgetName(String budgetName) { this.budgetName = budgetName; }

    public String getBudgetDescription() { return budgetDescription; }
    public void setBudgetDescription(String budgetDescription) { this.budgetDescription = budgetDescription; }

    public Double getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(Double budgetAmount) { this.budgetAmount = budgetAmount; }

    public String getBudgetPeriod() { return budgetPeriod; }
    public void setBudgetPeriod(String budgetPeriod) { this.budgetPeriod = budgetPeriod; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }


}
