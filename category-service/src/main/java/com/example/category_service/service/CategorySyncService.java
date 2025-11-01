package com.example.category_service.service;

import com.example.category_service.entity.sqlite.SQLiteCategory;
import com.example.category_service.entity.oracle.OracleCategory;
import com.example.category_service.repository.oracle.OracleCategoryRepository;
import com.example.category_service.repository.sqlite.SQLiteCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class CategorySyncService {

    @Autowired
    private SQLiteCategoryRepository sqliteRepo;

    @Autowired
    private OracleCategoryRepository oracleRepo;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void syncCategories() {
        List<SQLiteCategory> sqliteCategories = sqliteRepo.findAll();
        List<OracleCategory> oracleCategories = oracleRepo.findAll();

        // ✅ Convert SQLite categories into a map for faster lookup
        Map<String, SQLiteCategory> sqliteMap = sqliteCategories.stream()
                .collect(Collectors.toMap(SQLiteCategory::getCategoryName, c -> c));

        // ✅ 1. Handle Inserts and Updates
        for (SQLiteCategory sqliteCat : sqliteCategories) {
            Optional<OracleCategory> existingOpt = oracleRepo.findByCategoryName(sqliteCat.getCategoryName());

            if (existingOpt.isPresent()) {
                OracleCategory oracleCat = existingOpt.get();

                // Only update if something changed
                if (!Objects.equals(oracleCat.getCategoryType(), sqliteCat.getCategoryType()) ||
                        !Objects.equals(oracleCat.getDescription(), sqliteCat.getDescription())) {

                    oracleCat.setCategoryType(sqliteCat.getCategoryType());
                    oracleCat.setDescription(sqliteCat.getDescription());
                    oracleCat.setCreatedAt(sqliteCat.getCreatedAt());
                    oracleRepo.save(oracleCat);
                    System.out.println("🔄 Updated category in Oracle: " + sqliteCat.getCategoryName());
                }
            } else {
                // Insert new category
                OracleCategory newCat = new OracleCategory();
                newCat.setCategoryName(sqliteCat.getCategoryName());
                newCat.setCategoryType(sqliteCat.getCategoryType());
                newCat.setDescription(sqliteCat.getDescription());
                newCat.setCreatedAt(sqliteCat.getCreatedAt());
                oracleRepo.save(newCat);
                System.out.println("➕ Added new category in Oracle: " + sqliteCat.getCategoryName());
            }

            // Mark as synced
            sqliteCat.setIsSynced(true);
            sqliteRepo.save(sqliteCat);
        }

        // ✅ 2. Handle Deletions — remove Oracle records missing from SQLite
        Set<String> sqliteNames = sqliteMap.keySet();
        for (OracleCategory oracleCat : oracleCategories) {
            if (!sqliteNames.contains(oracleCat.getCategoryName())) {
                oracleRepo.delete(oracleCat);
                System.out.println("❌ Deleted from Oracle (not found in SQLite): " + oracleCat.getCategoryName());
            }
        }

        System.out.println("✅ Sync completed: Inserts/Updates/Deletes handled.");
    }
}
