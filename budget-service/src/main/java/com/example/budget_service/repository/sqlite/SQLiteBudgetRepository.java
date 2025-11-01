package com.example.budget_service.repository.sqlite;

import com.example.budget_service.entity.sqlite.SQLiteBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLiteBudgetRepository extends JpaRepository<SQLiteBudget, Integer> {
    List<SQLiteBudget> findByUserId(int userId);
    List<SQLiteBudget> findByCategoryId(int categoryId);
    List<SQLiteBudget> findByIsSyncedFalse();
}
