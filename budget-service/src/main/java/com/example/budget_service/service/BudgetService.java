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
        return sqliteBudgetRepository.save(budget);
    }

    // READ - all
    public List<SQLiteBudget> getAllBudgets() {
        return sqliteBudgetRepository.findAll();
    }

    // READ - by ID
    public Optional<SQLiteBudget> getBudgetById(int id) {
        return sqliteBudgetRepository.findById(id);
    }

    // UPDATE
    public SQLiteBudget updateBudget(int id, SQLiteBudget updatedBudget) {
        return sqliteBudgetRepository.findById(id)
                .map(existing -> {
                    existing.setUserId(updatedBudget.getUserId());
                    existing.setCategoryId(updatedBudget.getCategoryId());
                    existing.setBudgetName(updatedBudget.getBudgetName());
                    existing.setBudgetDescription(updatedBudget.getBudgetDescription());
                    existing.setBudgetAmount(updatedBudget.getBudgetAmount());
                    existing.setBudgetPeriod(updatedBudget.getBudgetPeriod());
                    existing.setStartDate(updatedBudget.getStartDate());
                    existing.setEndDate(updatedBudget.getEndDate());
                    existing.setIsSynced(updatedBudget.getIsSynced());
                    return sqliteBudgetRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + id));
    }

    // READ - by User ID
    public List<SQLiteBudget> getBudgetsByUserId(int userId) {
        return sqliteBudgetRepository.findByUserId(userId);
    }

    // DELETE
    public void deleteBudget(int id) {
        if (!sqliteBudgetRepository.existsById(id)) {
            throw new RuntimeException("Budget not found with ID: " + id);
        }
        sqliteBudgetRepository.deleteById(id);
    }
}
