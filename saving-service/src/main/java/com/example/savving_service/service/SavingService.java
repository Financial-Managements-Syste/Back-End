package com.example.savving_service.service;

import com.example.savving_service.entity.sqlite.SQLiteSavingsGoal;
import com.example.savving_service.repository.sqlite.SQLiteSavingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingService {

    @Autowired
    private SQLiteSavingRepository sqliteSavingRepository;

    // Create new saving goal
    public SQLiteSavingsGoal createSavingGoal(SQLiteSavingsGoal savingGoal) {
        // Default values if not provided
        if (savingGoal.getStatus() == null || savingGoal.getStatus().isEmpty()) {
            savingGoal.setStatus("Active");
        }
        if (savingGoal.getCurrentAmount() < 0) {
            savingGoal.setCurrentAmount(0);
        }
        if (savingGoal.getSyncStatus() == null || savingGoal.getSyncStatus().isEmpty()) {
            savingGoal.setSyncStatus("NEW");
        }
        savingGoal.setIsSynced(0);
        savingGoal.setIsDeleted(0);

        return sqliteSavingRepository.save(savingGoal);
    }

    // Get all savings
    public List<SQLiteSavingsGoal> getAllSavings() {
        return sqliteSavingRepository.findAll();
    }

    // Get saving goal by ID
    public SQLiteSavingsGoal getSavingById(int id) {
        return sqliteSavingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + id));
    }

    // Get all savings by user ID
    public List<SQLiteSavingsGoal> getSavingsByUserId(int userId) {
        return sqliteSavingRepository.findByUserId(userId);
    }

    // Update existing saving goal (updates all columns)
    // Update existing saving goal (updates all columns except createdAt)
    public SQLiteSavingsGoal updateSavingGoal(int id, SQLiteSavingsGoal updatedGoal) {
        return sqliteSavingRepository.findById(id).map(existingGoal -> {
            existingGoal.setUserId(updatedGoal.getUserId());
            existingGoal.setGoalName(updatedGoal.getGoalName());
            existingGoal.setTargetAmount(updatedGoal.getTargetAmount());
            existingGoal.setCurrentAmount(updatedGoal.getCurrentAmount());
            existingGoal.setTargetDate(updatedGoal.getTargetDate());
            existingGoal.setStatus(updatedGoal.getStatus());
            existingGoal.setIsSynced(updatedGoal.getIsSynced());
            existingGoal.setSyncStatus(updatedGoal.getSyncStatus());
            existingGoal.setIsDeleted(updatedGoal.getIsDeleted());

            // Keep createdAt as-is
            // existingGoal.setCreatedAt(updatedGoal.getCreatedAt());

            // Let @PreUpdate handle updatedAt automatically
            // Or set manually if needed:
            existingGoal.setUpdatedAt(updatedGoal.getUpdatedAt() != null ? updatedGoal.getUpdatedAt() : java.time.LocalDateTime.now());

            return sqliteSavingRepository.save(existingGoal);
        }).orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + id));
    }


    // Delete saving goal (soft delete)
    public void deleteSavingGoal(int id) {
        sqliteSavingRepository.findById(id).map(existingGoal -> {
            // Soft delete
            existingGoal.setIsDeleted(1);
            existingGoal.setIsSynced(0);
            existingGoal.setSyncStatus("DELETED");
            return sqliteSavingRepository.save(existingGoal);
        }).orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + id));
    }

    // Add funds to saving goal
    public SQLiteSavingsGoal addFunds(int goalId, double amount) {
        return sqliteSavingRepository.findById(goalId).map(goal -> {
            double newAmount = goal.getCurrentAmount() + amount;
            goal.setCurrentAmount(newAmount);

            // Auto-complete if target is reached
            if (newAmount >= goal.getTargetAmount()) {
                goal.setStatus("Completed");
            }

            // Mark for sync
            goal.setIsSynced(0);
            goal.setSyncStatus("UPDATED");

            return sqliteSavingRepository.save(goal);
        }).orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + goalId));
    }

    // Calculate goal progress percentage
    public double calculateProgress(int goalId) {
        SQLiteSavingsGoal goal = getSavingById(goalId);
        if (goal.getTargetAmount() == 0) return 0;
        return (goal.getCurrentAmount() / goal.getTargetAmount()) * 100.0;
    }
}
