package com.example.category_service.service;

import com.example.category_service.entity.sqlite.SQLiteCategory;
import com.example.category_service.repository.oracle.OracleCategoryRepository;
import com.example.category_service.repository.sqlite.SQLiteCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CategorySyncService {

    @Autowired
    private SQLiteCategoryRepository sqliteRepo;

    @Autowired
    private OracleCategoryRepository oracleRepo;

    // Master sync process (can be called manually or from a scheduler)
    public void syncCategories() {
        System.out.println("⏳ [Sync] Starting full category sync process...");

        syncInserts();
        syncUpdates();
        syncDeletes();

        System.out.println("✅ [Sync] Full category sync completed successfully.");
    }

    // --- INSERT SYNC ---
    public void syncInserts() {
        List<SQLiteCategory> newCategories = sqliteRepo.findBySyncStatus("NEW");

        for (SQLiteCategory c : newCategories) {
            try {
                oracleRepo.insertCategoryFromSQLite(
                        c.getCategoryName(),
                        c.getCategoryType(),
                        c.getDescription(),
                        Timestamp.valueOf(c.getCreatedAt())
                );

                c.setIsSynced(1);
                c.setSyncStatus("SYNCED");
                sqliteRepo.save(c);

                System.out.println("✅ [Insert Sync] Synced category ID: " + c.getCategoryId());
            } catch (Exception e) {
                System.err.println("❌ [Insert Sync] Failed for Category ID " + c.getCategoryId() + ": " + e.getMessage());
            }
        }
    }

    // --- UPDATE SYNC ---
    public void syncUpdates() {
        List<SQLiteCategory> updatedCategories = sqliteRepo.findBySyncStatus("UPDATED");

        for (SQLiteCategory c : updatedCategories) {
            try {
                oracleRepo.updateCategoryFromSQLite(
                        c.getCategoryId(),
                        c.getCategoryName(),
                        c.getCategoryType(),
                        c.getDescription()
                );

                c.setIsSynced(1);
                c.setSyncStatus("SYNCED");
                sqliteRepo.save(c);

                System.out.println("✅ [Update Sync] Synced category ID: " + c.getCategoryId());
            } catch (Exception e) {
                System.err.println("❌ [Update Sync] Failed for Category ID " + c.getCategoryId() + ": " + e.getMessage());
            }
        }
    }

    // --- DELETE SYNC ---
    public void syncDeletes() {
        List<SQLiteCategory> deletedCategories = sqliteRepo.findBySyncStatus("DELETED");

        for (SQLiteCategory c : deletedCategories) {
            try {
                oracleRepo.deleteCategoryFromSQLite(c.getCategoryId());

                // Option 1: Delete from SQLite
                sqliteRepo.delete(c);

                // Option 2: Keep record but mark as synced
                // c.setIsSynced(1);
                // c.setSyncStatus("SYNCED");
                // sqliteRepo.save(c);

                System.out.println("🗑️ [Delete Sync] Deleted category ID: " + c.getCategoryId());
            } catch (Exception e) {
                System.err.println("❌ [Delete Sync] Failed for Category ID " + c.getCategoryId() + ": " + e.getMessage());
            }
        }
    }
}
