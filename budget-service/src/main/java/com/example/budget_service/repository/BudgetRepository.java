package com.example.budget_service.repository;

import com.example.budget_service.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Find all budgets for a specific user
    List<Budget> findByUserId(Long userId);

}
