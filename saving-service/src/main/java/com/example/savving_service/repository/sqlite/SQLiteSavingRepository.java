package com.example.savving_service.repository.sqlite;

import com.example.savving_service.entity.sqlite.SQLiteSavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLiteSavingRepository extends JpaRepository<SQLiteSavingsGoal, Integer> {
    List<SQLiteSavingsGoal> findByUserId(int userId);

    // Sync status queries
    List<SQLiteSavingsGoal> findBySyncStatus(String syncStatus);
}
