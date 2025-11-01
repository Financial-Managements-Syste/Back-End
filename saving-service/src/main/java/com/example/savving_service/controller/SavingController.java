package com.example.savving_service.controller;

import com.example.savving_service.entity.sqlite.SQLiteSavingsGoal;
import com.example.savving_service.service.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
@CrossOrigin(origins = "*")
public class SavingController {

    @Autowired
    private SavingService savingService;

    //  Create a new saving goal
    @PostMapping
    public SQLiteSavingsGoal createSavingGoal(@RequestBody SQLiteSavingsGoal SQLiteSavingsGoal) {
        return savingService.createSavingGoal(SQLiteSavingsGoal);
    }

    //  Get all saving goals
    @GetMapping
    public List<SQLiteSavingsGoal> getAllSavings() {
        return savingService.getAllSavings();
    }

    //  Get saving goal by ID
    @GetMapping("/{id}")
    public SQLiteSavingsGoal getSavingById(@PathVariable int id) {
        return savingService.getSavingById(id);
    }

    //  Get all saving goals for a user
    @GetMapping("/user/{userId}")
    public List<SQLiteSavingsGoal> getSavingsByUserId(@PathVariable int userId) {
        return savingService.getSavingsByUserId(userId);
    }

    // Update saving goal
    @PutMapping("/{id}")
    public SQLiteSavingsGoal updateSavingGoal(@PathVariable int id, @RequestBody SQLiteSavingsGoal SQLiteSavingsGoal) {
        return savingService.updateSavingGoal(id, SQLiteSavingsGoal);
    }

    //  Delete saving goal
    @DeleteMapping("/{id}")
    public String deleteSavingGoal(@PathVariable int id) {
        savingService.deleteSavingGoal(id);
        return "Saving goal deleted successfully!";
    }

    //  Add funds to a goal
    @PutMapping("/{id}/add-funds")
    public SQLiteSavingsGoal addFunds(@PathVariable int id, @RequestParam double amount) {
        return savingService.addFunds(id, amount);
    }

    //  Get progress percentage
    @GetMapping("/{id}/progress")
    public double getProgress(@PathVariable int id) {
        return savingService.calculateProgress(id);
    }
}
