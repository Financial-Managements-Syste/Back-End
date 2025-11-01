package com.example.savving_service.service;

import com.example.savving_service.entity.sqlite.SQLiteSavingsGoal;
import com.example.savving_service.repository.sqlite.SQLiteSavingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingService {

    @Autowired
    private SQLiteSavingRepository SQLiteSavingRepository;

    // Create new saving goal
    public SQLiteSavingsGoal createSavingGoal(SQLiteSavingsGoal SQLiteSavingsGoal) {
        // Default values if not provided
        if (SQLiteSavingsGoal.getStatus() == null || SQLiteSavingsGoal.getStatus().isEmpty()) {
            SQLiteSavingsGoal.setStatus("Active");
        }
        if (SQLiteSavingsGoal.getCurrentAmount() == 0) {
            SQLiteSavingsGoal.setCurrentAmount(0);
        }
        return SQLiteSavingRepository.save(SQLiteSavingsGoal);
    }

    //  Get all savings
    public List<SQLiteSavingsGoal> getAllSavings() {
        return SQLiteSavingRepository.findAll();
    }

    //  Get saving goal by ID
    public SQLiteSavingsGoal getSavingById(int id) {
        return SQLiteSavingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + id));
    }

    //  Get all savings by user ID
    public List<SQLiteSavingsGoal> getSavingsByUserId(int userId) {
        return SQLiteSavingRepository.findByUserId(userId);
    }

    //  Update existing saving goal
    public SQLiteSavingsGoal updateSavingGoal(int id, SQLiteSavingsGoal updatedSQLiteSavingsGoal) {
        return SQLiteSavingRepository.findById(id).map(SQLiteSavingsGoal -> {
            SQLiteSavingsGoal.setGoalName(updatedSQLiteSavingsGoal.getGoalName());
            SQLiteSavingsGoal.setTargetAmount(updatedSQLiteSavingsGoal.getTargetAmount());
            SQLiteSavingsGoal.setCurrentAmount(updatedSQLiteSavingsGoal.getCurrentAmount());
            SQLiteSavingsGoal.setTargetDate(updatedSQLiteSavingsGoal.getTargetDate());
            SQLiteSavingsGoal.setStatus(updatedSQLiteSavingsGoal.getStatus());
            SQLiteSavingsGoal.setIsSynced(updatedSQLiteSavingsGoal.getIsSynced());
            return SQLiteSavingRepository.save(SQLiteSavingsGoal);
        }).orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + id));
    }

    // Delete saving goal
    public void deleteSavingGoal(int id) {
        if (!SQLiteSavingRepository.existsById(id)) {
            throw new RuntimeException("Saving goal not found with ID: " + id);
        }
        SQLiteSavingRepository.deleteById(id);
    }

    // Add funds to saving goal
    public SQLiteSavingsGoal addFunds(int goalId, double amount) {
        return SQLiteSavingRepository.findById(goalId).map(SQLiteSavingsGoal -> {
            double newAmount = SQLiteSavingsGoal.getCurrentAmount() + amount;
            SQLiteSavingsGoal.setCurrentAmount(newAmount);

            // Auto-complete if target is reached
            if (newAmount >= SQLiteSavingsGoal.getTargetAmount()) {
                SQLiteSavingsGoal.setStatus("Completed");
            }

            return SQLiteSavingRepository.save(SQLiteSavingsGoal);
        }).orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + goalId));
    }

    //  Calculate goal progress percentage
    public double calculateProgress(int goalId) {
        SQLiteSavingsGoal SQLiteSavingsGoal = getSavingById(goalId);
        if (SQLiteSavingsGoal.getTargetAmount() == 0) return 0;
        return (SQLiteSavingsGoal.getCurrentAmount() / SQLiteSavingsGoal.getTargetAmount()) * 100.0;
    }
}
