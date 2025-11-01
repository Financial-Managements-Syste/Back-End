package com.example.budget_service.repository.oracle;

import com.example.budget_service.entity.oracle.OracleBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OracleBudgetRepository extends JpaRepository<OracleBudget, Long> {
    boolean existsByBudgetName(String budgetName);
}
