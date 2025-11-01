package com.example.budget_service.service;

import com.example.budget_service.entity.sqlite.SQLiteBudget;
import com.example.budget_service.entity.oracle.OracleBudget;
import com.example.budget_service.repository.sqlite.SQLiteBudgetRepository;
import com.example.budget_service.repository.oracle.OracleBudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class BudgetSyncService {

    private static final Logger log = LoggerFactory.getLogger(BudgetSyncService.class);

    @Autowired
    private SQLiteBudgetRepository sqliteRepo;

    @Autowired
    private OracleBudgetRepository oracleRepo;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional(transactionManager = "oracleTransactionManager")
    public void syncBudgets() {
        List<SQLiteBudget> sqliteBudgets = sqliteRepo.findAll();
        List<OracleBudget> oracleBudgets = oracleRepo.findAll();

        int inserted = 0, updated = 0, deleted = 0, failed = 0;

        Map<String, OracleBudget> oracleMap = new HashMap<>();
        for (OracleBudget ob : oracleBudgets) {
            String key = generateKey(ob.getUserId(), ob.getCategoryId(), ob.getBudgetName());
            oracleMap.put(key, ob);
        }

        // --- INSERT / UPDATE ---
        for (SQLiteBudget sb : sqliteBudgets) {
            String key = generateKey((long) sb.getUserId(), (long) sb.getCategoryId(), sb.getBudgetName());
            OracleBudget ob = oracleMap.get(key);

            try {
                LocalDate startDate = parseDate(sb.getStartDate());
                LocalDate endDate = parseDate(sb.getEndDate());

                if (ob == null) {
                    // INSERT
                    OracleBudget newBudget = new OracleBudget();
                    newBudget.setUserId((long) sb.getUserId());
                    newBudget.setCategoryId((long) sb.getCategoryId());
                    newBudget.setBudgetName(sb.getBudgetName());
                    newBudget.setBudgetDescription(sb.getBudgetDescription());
                    newBudget.setBudgetAmount(sb.getBudgetAmount());
                    newBudget.setBudgetPeriod(sb.getBudgetPeriod());
                    newBudget.setStartDate(startDate);
                    newBudget.setEndDate(endDate);
                    newBudget.setCreatedAt(LocalDateTime.now());
                    newBudget.setUpdatedAt(LocalDateTime.now());

                    oracleRepo.save(newBudget);

                    sb.setIsSynced(1);
                    sqliteRepo.save(sb);
                    inserted++;
                } else {
                    // UPDATE
                    boolean changed = false;
                    if (!Objects.equals(ob.getBudgetAmount(), sb.getBudgetAmount())) {
                        ob.setBudgetAmount(sb.getBudgetAmount());
                        changed = true;
                    }
                    if (!Objects.equals(ob.getBudgetPeriod(), sb.getBudgetPeriod())) {
                        ob.setBudgetPeriod(sb.getBudgetPeriod());
                        changed = true;
                    }
                    if (!Objects.equals(ob.getBudgetDescription(), sb.getBudgetDescription())) {
                        ob.setBudgetDescription(sb.getBudgetDescription());
                        changed = true;
                    }
                    if (!Objects.equals(ob.getStartDate(), startDate)) {
                        ob.setStartDate(startDate);
                        changed = true;
                    }
                    if (!Objects.equals(ob.getEndDate(), endDate)) {
                        ob.setEndDate(endDate);
                        changed = true;
                    }

                    if (changed) {
                        ob.setUpdatedAt(LocalDateTime.now());
                        oracleRepo.save(ob);
                    }

                    sb.setIsSynced(1);
                    sqliteRepo.save(sb);
                    updated++;
                }
            } catch (Exception e) {
                failed++;
                log.error("Failed to sync SQLite budget id={} to Oracle: {}", sb.getBudgetId(), e.getMessage());
            }
        }

        // --- DELETE Oracle budgets not in SQLite ---
        Set<String> sqliteKeys = new HashSet<>();
        for (SQLiteBudget sb : sqliteBudgets) {
            sqliteKeys.add(generateKey((long) sb.getUserId(), (long) sb.getCategoryId(), sb.getBudgetName()));
        }

        for (OracleBudget ob : oracleBudgets) {
            String key = generateKey(ob.getUserId(), ob.getCategoryId(), ob.getBudgetName());
            if (!sqliteKeys.contains(key)) {
                try {
                    oracleRepo.delete(ob);
                    deleted++;
                } catch (Exception e) {
                    failed++;
                    log.error("Failed to delete Oracle budget id={} : {}", ob.getBudgetId(), e.getMessage());
                }
            }
        }

        log.info("Budget sync completed. Inserted: {}, Updated: {}, Deleted: {}, Failed: {}", inserted, updated, deleted, failed);
    }

    private String generateKey(Long userId, Long categoryId, String budgetName) {
        return userId + "_" + categoryId + "_" + budgetName;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            log.warn("Invalid date format '{}', using current date", dateStr);
            return LocalDate.now();
        }
    }
}
