package com.example.budget_service.entity.sqlite;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Budgets")
public class SQLiteBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private int budgetId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "budget_name")
    private String budgetName;

    @Column(name = "budget_description")
    private String budgetDescription;

    @Column(name = "budget_amount")
    private double budgetAmount;

    @Column(name = "budget_period")
    private String budgetPeriod;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_synced")
    private int isSynced;

    @Column(name = "sync_status")
    private String syncStatus; // NEW, UPDATED, DELETED, SYNCED

    @Column(name = "is_deleted")
    private int isDeleted; // 0 = not deleted, 1 = deleted


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getBudgetName() { return budgetName; }
    public void setBudgetName(String budgetName) { this.budgetName = budgetName; }

    public String getBudgetDescription() { return budgetDescription; }
    public void setBudgetDescription(String budgetDescription) { this.budgetDescription = budgetDescription; }

    public double getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(double budgetAmount) { this.budgetAmount = budgetAmount; }

    public String getBudgetPeriod() { return budgetPeriod; }
    public void setBudgetPeriod(String budgetPeriod) { this.budgetPeriod = budgetPeriod; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getIsSynced() { return isSynced; }
    public void setIsSynced(int isSynced) { this.isSynced = isSynced; }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
