package com.example.savving_service.service;

import com.example.savving_service.entity.SavingsGoals;
import com.example.savving_service.repository.SavingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingService {

    @Autowired
    private SavingRepository savingRepository;

    // Create new saving goal
    public SavingsGoals createSavingGoal(SavingsGoals savingsGoals) {
        // Default values if not provided
        if (savingsGoals.getStatus() == null || savingsGoals.getStatus().isEmpty()) {
            savingsGoals.setStatus("Active");
        }
        if (savingsGoals.getCurrentAmount() == 0) {
            savingsGoals.setCurrentAmount(0);
        }
        return savingRepository.save(savingsGoals);
    }

    //  Get all savings
    public List<SavingsGoals> getAllSavings() {
        return savingRepository.findAll();
    }

    //  Get saving goal by ID
    public SavingsGoals getSavingById(int id) {
        return savingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + id));
    }

    //  Get all savings by user ID
    public List<SavingsGoals> getSavingsByUserId(int userId) {
        return savingRepository.findByUserId(userId);
    }

    //  Update existing saving goal
    public SavingsGoals updateSavingGoal(int id, SavingsGoals updatedSavingsGoals) {
        return savingRepository.findById(id).map(savingsGoals -> {
            savingsGoals.setGoalName(updatedSavingsGoals.getGoalName());
            savingsGoals.setTargetAmount(updatedSavingsGoals.getTargetAmount());
            savingsGoals.setCurrentAmount(updatedSavingsGoals.getCurrentAmount());
            savingsGoals.setTargetDate(updatedSavingsGoals.getTargetDate());
            savingsGoals.setStatus(updatedSavingsGoals.getStatus());
            savingsGoals.setIsSynced(updatedSavingsGoals.getIsSynced());
            return savingRepository.save(savingsGoals);
        }).orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + id));
    }

    // Delete saving goal
    public void deleteSavingGoal(int id) {
        if (!savingRepository.existsById(id)) {
            throw new RuntimeException("Saving goal not found with ID: " + id);
        }
        savingRepository.deleteById(id);
    }

    // Add funds to saving goal
    public SavingsGoals addFunds(int goalId, double amount) {
        return savingRepository.findById(goalId).map(savingsGoals -> {
            double newAmount = savingsGoals.getCurrentAmount() + amount;
            savingsGoals.setCurrentAmount(newAmount);

            // Auto-complete if target is reached
            if (newAmount >= savingsGoals.getTargetAmount()) {
                savingsGoals.setStatus("Completed");
            }

            return savingRepository.save(savingsGoals);
        }).orElseThrow(() -> new RuntimeException("Saving goal not found with ID: " + goalId));
    }

    //  Calculate goal progress percentage
    public double calculateProgress(int goalId) {
        SavingsGoals savingsGoals = getSavingById(goalId);
        if (savingsGoals.getTargetAmount() == 0) return 0;
        return (savingsGoals.getCurrentAmount() / savingsGoals.getTargetAmount()) * 100.0;
    }
}
