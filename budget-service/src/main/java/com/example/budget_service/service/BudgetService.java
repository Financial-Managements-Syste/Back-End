package com.example.budget_service.service;

import com.example.budget_service.entity.Budget;
import com.example.budget_service.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    // CREATE
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    // READ - all
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    // READ - by ID
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    // UPDATE
    public Budget updateBudget(Long id, Budget updatedBudget) {
        return budgetRepository.findById(id)
                .map(existing -> {
                    existing.setUserId(updatedBudget.getUserId());
                    existing.setCategoryId(updatedBudget.getCategoryId());
                    existing.setBudgetName(updatedBudget.getBudgetName());           // updated field
                    existing.setBudgetDescription(updatedBudget.getBudgetDescription()); // updated field
                    existing.setBudgetAmount(updatedBudget.getBudgetAmount());
                    existing.setBudgetPeriod(updatedBudget.getBudgetPeriod());
                    existing.setStartDate(updatedBudget.getStartDate());
                    existing.setEndDate(updatedBudget.getEndDate());
                    existing.setIsSynced(updatedBudget.getIsSynced());
                    return budgetRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + id));
    }

    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    // DELETE
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new RuntimeException("Budget not found with ID: " + id);
        }
        budgetRepository.deleteById(id);
    }
}
