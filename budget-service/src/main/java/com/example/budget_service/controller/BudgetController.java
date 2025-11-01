package com.example.budget_service.controller;

import com.example.budget_service.entity.sqlite.SQLiteBudget;
import com.example.budget_service.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    // CREATE
    @PostMapping
    public ResponseEntity<SQLiteBudget> createBudget(@RequestBody SQLiteBudget budget) {
        SQLiteBudget saved = budgetService.createBudget(budget);
        return ResponseEntity.ok(saved);
    }

    // READ - All
    @GetMapping
    public ResponseEntity<List<SQLiteBudget>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    // READ - By User
    @GetMapping(params = "user_id")
    public ResponseEntity<List<SQLiteBudget>> getBudgetsByUser(@RequestParam("user_id") int userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUserId(userId));
    }

    // READ - One by ID
    @GetMapping("/{id}")
    public ResponseEntity<SQLiteBudget> getBudgetById(@PathVariable int id) {
        return budgetService.getBudgetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<SQLiteBudget> updateBudget(@PathVariable int id, @RequestBody SQLiteBudget budget) {
        SQLiteBudget updated = budgetService.updateBudget(id, budget);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable int id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
