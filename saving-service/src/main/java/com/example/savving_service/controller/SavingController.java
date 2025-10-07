package com.example.savving_service.controller;

import com.example.savving_service.entity.SavingsGoals;
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
    public SavingsGoals createSavingGoal(@RequestBody SavingsGoals savingsGoals) {
        return savingService.createSavingGoal(savingsGoals);
    }

    //  Get all saving goals
    @GetMapping
    public List<SavingsGoals> getAllSavings() {
        return savingService.getAllSavings();
    }

    //  Get saving goal by ID
    @GetMapping("/{id}")
    public SavingsGoals getSavingById(@PathVariable int id) {
        return savingService.getSavingById(id);
    }

    //  Get all saving goals for a user
    @GetMapping("/user/{userId}")
    public List<SavingsGoals> getSavingsByUserId(@PathVariable int userId) {
        return savingService.getSavingsByUserId(userId);
    }

    // Update saving goal
    @PutMapping("/{id}")
    public SavingsGoals updateSavingGoal(@PathVariable int id, @RequestBody SavingsGoals savingsGoals) {
        return savingService.updateSavingGoal(id, savingsGoals);
    }

    //  Delete saving goal
    @DeleteMapping("/{id}")
    public String deleteSavingGoal(@PathVariable int id) {
        savingService.deleteSavingGoal(id);
        return "Saving goal deleted successfully!";
    }

    //  Add funds to a goal
    @PutMapping("/{id}/add-funds")
    public SavingsGoals addFunds(@PathVariable int id, @RequestParam double amount) {
        return savingService.addFunds(id, amount);
    }

    //  Get progress percentage
    @GetMapping("/{id}/progress")
    public double getProgress(@PathVariable int id) {
        return savingService.calculateProgress(id);
    }
}
