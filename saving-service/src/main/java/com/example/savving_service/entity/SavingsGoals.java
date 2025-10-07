package com.example.savving_service.entity;


import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "SavingsGoals")
public class SavingsGoals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private int goalId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "goal_name", nullable = false)
    private String goalName;

    @Column(name = "target_amount", nullable = false)
    private double targetAmount;

    @Column(name = "current_amount", nullable = false)
    private double currentAmount;

    @Column(name = "target_date", nullable = false)
    @Convert(converter = LocalDateConverter.class) // ✅ Use converter
    private LocalDate targetDate;

    @Column(name = "status")
    private String status;

    @Column(name = "is_synced")
    private int isSynced;

    // Constructors, getters, setters
    public SavingsGoals() {}

    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getIsSynced() { return isSynced; }
    public void setIsSynced(int isSynced) { this.isSynced = isSynced; }
}
