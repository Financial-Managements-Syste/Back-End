package com.example.budget_service.service;

import com.example.budget_service.entity.sqlite.SQLiteBudget;
import com.example.budget_service.repository.sqlite.SQLiteBudgetRepository;
import com.example.budget_service.repository.oracle.OracleBudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Service
public class BudgetSyncService {

    @Autowired
    private SQLiteBudgetRepository sqliteRepo;

    @Autowired
    private OracleBudgetRepository oracleRepo;

    // Master sync process (can be called from scheduler or manually)
    public void syncBudgets() {
        System.out.println("⏳ [Sync] Starting full budget sync process...");

        syncInserts();
        syncUpdates();
        syncDeletes();

        System.out.println("✅ [Sync] Full budget sync completed successfully.");
    }

    // --- INSERT SYNC ---
    public void syncInserts() {
        List<SQLiteBudget> newBudgets = sqliteRepo.findBySyncStatus("NEW");

        for (SQLiteBudget b : newBudgets) {
            try {
                oracleRepo.insertBudgetFromSQLite(
                        (long) b.getUserId(),
                        (long) b.getCategoryId(),
                        b.getBudgetName(),
                        b.getBudgetDescription(),
                        b.getBudgetAmount(),
                        b.getBudgetPeriod(),
                        Date.valueOf(b.getStartDate()),
                        Date.valueOf(b.getEndDate()),
                        Timestamp.valueOf(b.getCreatedAt()),
                        Timestamp.valueOf(b.getUpdatedAt())
                );

                // Mark as synced
                b.setIsSynced(1);
                b.setSyncStatus("SYNCED");
                sqliteRepo.save(b);

                System.out.println("✅ [Insert Sync] Synced budget ID: " + b.getBudgetId());
            } catch (Exception e) {
                System.err.println("❌ [Insert Sync] Failed for Budget ID " + b.getBudgetId() + ": " + e.getMessage());
            }
        }
    }

    // --- UPDATE SYNC ---
    public void syncUpdates() {
        List<SQLiteBudget> updatedBudgets = sqliteRepo.findBySyncStatus("UPDATED");

        for (SQLiteBudget b : updatedBudgets) {
            try {
                oracleRepo.updateBudgetFromSQLite(
                        (long) b.getBudgetId(),
                        (long) b.getUserId(),
                        (long) b.getCategoryId(),
                        b.getBudgetName(),
                        b.getBudgetDescription(),
                        b.getBudgetAmount(),
                        b.getBudgetPeriod(),
                        Date.valueOf(b.getStartDate()),
                        Date.valueOf(b.getEndDate()),
                        Timestamp.valueOf(b.getUpdatedAt())
                );

                b.setIsSynced(1);
                b.setSyncStatus("SYNCED");
                sqliteRepo.save(b);

                System.out.println("✅ [Update Sync] Synced budget ID: " + b.getBudgetId());
            } catch (Exception e) {
                System.err.println("❌ [Update Sync] Failed for Budget ID " + b.getBudgetId() + ": " + e.getMessage());
            }
        }
    }

    // --- DELETE SYNC ---
    public void syncDeletes() {
        List<SQLiteBudget> deletedBudgets = sqliteRepo.findBySyncStatus("DELETED");

        for (SQLiteBudget b : deletedBudgets) {
            try {
                oracleRepo.deleteBudgetFromSQLite((long) b.getBudgetId());

                // After delete sync, remove or mark as permanently synced
                b.setIsSynced(1);
                b.setSyncStatus("SYNCED");
                sqliteRepo.delete(b);

                System.out.println("🗑️ [Delete Sync] Deleted budget ID: " + b.getBudgetId());
            } catch (Exception e) {
                System.err.println("❌ [Delete Sync] Failed for Budget ID " + b.getBudgetId() + ": " + e.getMessage());
            }
        }
    }
}
