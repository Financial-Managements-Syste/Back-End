package com.example.budget_service.service;

import com.example.budget_service.entity.sqlite.SQLiteBudget;
import com.example.budget_service.repository.sqlite.SQLiteBudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private SQLiteBudgetRepository sqliteBudgetRepository;

    // CREATE
    public SQLiteBudget createBudget(SQLiteBudget budget) {
        budget.setIsSynced(0); // Not yet synced
        budget.setSyncStatus("NEW");
        budget.setIsDeleted(0);
        return sqliteBudgetRepository.save(budget);
    }

    // READ - all (exclude deleted)
    public List<SQLiteBudget> getAllBudgets() {
        return sqliteBudgetRepository.findByIsDeleted(0);
    }

    // READ - by ID (exclude deleted)
    public Optional<SQLiteBudget> getBudgetById(int id) {
        Optional<SQLiteBudget> budget = sqliteBudgetRepository.findById(id);
        return budget.filter(b -> b.getIsDeleted() == 0);
    }

    // UPDATE
    public SQLiteBudget updateBudget(int id, SQLiteBudget updatedBudget) {
        return sqliteBudgetRepository.findById(id)
                .map(existing -> {
                    if (existing.getIsDeleted() == 1) {
                        throw new RuntimeException("Cannot update a deleted budget (ID: " + id + ")");
                    }

                    existing.setUserId(updatedBudget.getUserId());
                    existing.setCategoryId(updatedBudget.getCategoryId());
                    existing.setBudgetName(updatedBudget.getBudgetName());
                    existing.setBudgetDescription(updatedBudget.getBudgetDescription());
                    existing.setBudgetAmount(updatedBudget.getBudgetAmount());
                    existing.setBudgetPeriod(updatedBudget.getBudgetPeriod());
                    existing.setStartDate(updatedBudget.getStartDate());
                    existing.setEndDate(updatedBudget.getEndDate());

                    existing.setIsSynced(0); // Mark for sync
                    existing.setSyncStatus("UPDATED");
                    return sqliteBudgetRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + id));
    }

    // READ - by User ID (exclude deleted)
    public List<SQLiteBudget> getBudgetsByUserId(int userId) {
        return sqliteBudgetRepository.findByUserIdAndIsDeleted(userId, 0);
    }

    // DELETE (soft delete)
    public void deleteBudget(int id) {
        SQLiteBudget budget = sqliteBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + id));

        // Mark as deleted instead of physically removing
        budget.setIsDeleted(1);
        budget.setIsSynced(0);
        budget.setSyncStatus("DELETED");

        sqliteBudgetRepository.save(budget);
    }

    // Helper methods for syncing
    public List<SQLiteBudget> getNewBudgets() {
        return sqliteBudgetRepository.findBySyncStatus("NEW");
    }

    public List<SQLiteBudget> getUpdatedBudgets() {
        return sqliteBudgetRepository.findBySyncStatus("UPDATED");
    }

    public List<SQLiteBudget> getDeletedBudgets() {
        return sqliteBudgetRepository.findBySyncStatus("DELETED");
    }

    // After successful sync, mark as synced
    public void markAsSynced(SQLiteBudget budget) {
        budget.setIsSynced(1);
        budget.setSyncStatus("SYNCED");

        // If it was a delete, we can optionally remove it
        if (budget.getIsDeleted() == 1) {
            sqliteBudgetRepository.delete(budget);
        } else {
            sqliteBudgetRepository.save(budget);
        }
    }
}
