package com.example.savving_service.service;

import com.example.savving_service.entity.sqlite.SQLiteSavingsGoal;
import com.example.savving_service.repository.oracle.OracleSavingsGoalRepository;
import com.example.savving_service.repository.sqlite.SQLiteSavingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavingsGoalSyncService {

    @Autowired
    private SQLiteSavingRepository sqliteRepo;

    @Autowired
    private OracleSavingsGoalRepository oracleRepo;

    public void syncSavingsGoals() {
        System.out.println("⏳ [Sync] Starting SavingsGoal sync...");

        syncInserts();
        syncUpdates();
        syncDeletes();

        System.out.println("✅ [Sync] SavingsGoal sync completed.");
    }

    // --- INSERT ---
    private void syncInserts() {
        List<SQLiteSavingsGoal> newGoals = sqliteRepo.findBySyncStatus("NEW");

        for (SQLiteSavingsGoal g : newGoals) {
            try {
                Timestamp createdAt = g.getCreatedAt() != null
                        ? Timestamp.valueOf(g.getCreatedAt())
                        : Timestamp.valueOf(LocalDateTime.now());

                Timestamp updatedAt = g.getUpdatedAt() != null
                        ? Timestamp.valueOf(g.getUpdatedAt())
                        : Timestamp.valueOf(LocalDateTime.now());

                oracleRepo.insertSavingFromSQLite(
                        (long) g.getUserId(),
                        g.getGoalName(),
                        g.getTargetAmount(),
                        g.getCurrentAmount(),
                        Date.valueOf(g.getTargetDate()),
                        createdAt,
                        updatedAt,
                        g.getStatus()
                );

                g.setIsSynced(1);
                g.setSyncStatus("SYNCED");
                sqliteRepo.save(g);

                System.out.println("✅ [Insert Sync] Synced goal ID: " + g.getGoalId());
            } catch (Exception e) {
                System.err.println("❌ [Insert Sync] Failed for goal ID " + g.getGoalId() + ": " + e.getMessage());
            }
        }
    }

    // --- UPDATE ---
    private void syncUpdates() {
        List<SQLiteSavingsGoal> updatedGoals = sqliteRepo.findBySyncStatus("UPDATED");

        for (SQLiteSavingsGoal g : updatedGoals) {
            try {
                Timestamp updatedAt = g.getUpdatedAt() != null
                        ? Timestamp.valueOf(g.getUpdatedAt())
                        : Timestamp.valueOf(LocalDateTime.now());

                oracleRepo.updateSavingFromSQLite(
                        (long) g.getGoalId(),
                        (long) g.getUserId(),
                        g.getGoalName(),
                        g.getTargetAmount(),
                        g.getCurrentAmount(),
                        Date.valueOf(g.getTargetDate()),
                        updatedAt,
                        g.getStatus()
                );

                g.setIsSynced(1);
                g.setSyncStatus("SYNCED");
                sqliteRepo.save(g);

                System.out.println("✅ [Update Sync] Synced goal ID: " + g.getGoalId());
            } catch (Exception e) {
                System.err.println("❌ [Update Sync] Failed for goal ID " + g.getGoalId() + ": " + e.getMessage());
            }
        }
    }

    // --- DELETE ---
    private void syncDeletes() {
        List<SQLiteSavingsGoal> deletedGoals = sqliteRepo.findBySyncStatus("DELETED");

        for (SQLiteSavingsGoal g : deletedGoals) {
            try {
                oracleRepo.deleteSavingFromSQLite((long) g.getGoalId());

                // Remove from SQLite
                sqliteRepo.delete(g);

                System.out.println("🗑️ [Delete Sync] Deleted goal ID: " + g.getGoalId());
            } catch (Exception e) {
                System.err.println("❌ [Delete Sync] Failed for goal ID " + g.getGoalId() + ": " + e.getMessage());
            }
        }
    }
}
