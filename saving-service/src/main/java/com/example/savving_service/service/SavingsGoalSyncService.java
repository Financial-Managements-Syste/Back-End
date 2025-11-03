package com.example.savving_service.service;

import com.example.savving_service.entity.oracle.OracleSavingsGoal;
import com.example.savving_service.entity.sqlite.SQLiteSavingsGoal;
import com.example.savving_service.repository.oracle.OracleSavingsGoalRepository;
import com.example.savving_service.repository.sqlite.SQLiteSavingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class SavingsGoalSyncService {

    @Autowired
    private SQLiteSavingRepository sqliteRepo;

    @Autowired
    private OracleSavingsGoalRepository oracleRepo;

    @Scheduled(fixedRate = 60000) // sync every 1 minute
    public void syncSavingsGoals() {
        List<SQLiteSavingsGoal> sqliteGoals = sqliteRepo.findAll();
        List<OracleSavingsGoal> oracleGoals = oracleRepo.findAll();

        // Map SQLite goals by goalId for fast lookup
        Map<Integer, SQLiteSavingsGoal> sqliteMap = sqliteGoals.stream()
                .collect(Collectors.toMap(SQLiteSavingsGoal::getGoalId, g -> g));

        // 1️⃣ Handle Inserts and Updates
        for (SQLiteSavingsGoal sqliteGoal : sqliteGoals) {
            Optional<OracleSavingsGoal> existingOpt = oracleRepo.findById((long) sqliteGoal.getGoalId());

            if (existingOpt.isPresent()) {
                OracleSavingsGoal oracleGoal = existingOpt.get();

                // Update only if something changed
                if (!Objects.equals(oracleGoal.getGoalName(), sqliteGoal.getGoalName()) ||
                        !Objects.equals(oracleGoal.getTargetAmount(), sqliteGoal.getTargetAmount()) ||
                        !Objects.equals(oracleGoal.getCurrentAmount(), sqliteGoal.getCurrentAmount()) ||
                        !Objects.equals(oracleGoal.getTargetDate(), sqliteGoal.getTargetDate()) ||
                        !Objects.equals(oracleGoal.getStatus(), sqliteGoal.getStatus())) {

                    oracleGoal.setGoalName(sqliteGoal.getGoalName());
                    oracleGoal.setUserId((long) sqliteGoal.getUserId());
                    oracleGoal.setTargetAmount(sqliteGoal.getTargetAmount());
                    oracleGoal.setCurrentAmount(sqliteGoal.getCurrentAmount());
                    oracleGoal.setTargetDate(sqliteGoal.getTargetDate());
                    oracleGoal.setCreatedAt(sqliteGoal.getCreatedAt());
                    oracleGoal.setUpdatedAt(sqliteGoal.getUpdatedAt());
                    oracleGoal.setStatus(sqliteGoal.getStatus());

                    oracleRepo.save(oracleGoal);
                    System.out.println("🔄 Updated Oracle SavingsGoal: " + sqliteGoal.getGoalName());
                }
            } else {
                // Insert new record
                OracleSavingsGoal newGoal = new OracleSavingsGoal();
                newGoal.setGoalId((long) sqliteGoal.getGoalId());
                newGoal.setUserId((long) sqliteGoal.getUserId());
                newGoal.setGoalName(sqliteGoal.getGoalName());
                newGoal.setTargetAmount(sqliteGoal.getTargetAmount());
                newGoal.setCurrentAmount(sqliteGoal.getCurrentAmount());
                newGoal.setTargetDate(sqliteGoal.getTargetDate());
                newGoal.setCreatedAt(sqliteGoal.getCreatedAt());
                newGoal.setUpdatedAt(sqliteGoal.getUpdatedAt());
                newGoal.setStatus(sqliteGoal.getStatus());

                oracleRepo.save(newGoal);
                System.out.println("➕ Added new Oracle SavingsGoal: " + sqliteGoal.getGoalName());
            }

            // Mark SQLite record as synced
            sqliteGoal.setIsSynced(1);
            sqliteRepo.save(sqliteGoal);
        }

        // 2️⃣ Handle Deletions — remove Oracle records missing from SQLite
        Set<Integer> sqliteIds = sqliteMap.keySet();
        for (OracleSavingsGoal oracleGoal : oracleGoals) {
            if (!sqliteIds.contains(oracleGoal.getGoalId().intValue())) {
                oracleRepo.delete(oracleGoal);
                System.out.println("❌ Deleted Oracle SavingsGoal (not in SQLite): " + oracleGoal.getGoalName());
            }
        }

        System.out.println("✅ SavingsGoal sync completed: Inserts/Updates/Deletes handled.");
    }
}