package com.example.sync.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IncrementalSyncService {

    private static final Logger log = LoggerFactory.getLogger(IncrementalSyncService.class);

    private final JdbcTemplate sqlite;
    private final JdbcTemplate oracle;

    public IncrementalSyncService(@Qualifier("sqliteJdbcTemplate") JdbcTemplate sqliteJdbcTemplate,
                                  @Qualifier("oracleJdbcTemplate") JdbcTemplate oracleJdbcTemplate) {
        this.sqlite = sqliteJdbcTemplate;
        this.oracle = oracleJdbcTemplate;
    }

    @Scheduled(initialDelay = 5000, fixedDelayString = "${app.sync.fixedDelayMs:300000}")
    public void runSync() {
        log.info("Starting incremental sync run...");
        syncUsers();
        syncCategories();
        syncTransactions();
        syncBudgets();
        syncSavingsGoals();
        log.info("Incremental sync run finished.");
    }

    private Timestamp getLastSyncedAt(String tableName) {
        List<Timestamp> res = oracle.query(
                "SELECT last_synced_at FROM Sync_Metadata WHERE table_name = ?",
                (rs, rn) -> rs.getTimestamp(1), tableName
        );
        return res.isEmpty() ? Timestamp.from(Instant.EPOCH) : res.get(0);
    }

    private void updateLastSyncedAt(String tableName, Timestamp ts) {
        int updated = oracle.update(
                "UPDATE Sync_Metadata SET last_synced_at = ? WHERE table_name = ?",
                ts, tableName
        );
        if (updated == 0) {
            oracle.update("INSERT INTO Sync_Metadata(table_name, last_synced_at) VALUES(?, ?)", tableName, ts);
        }
    }

    private Timestamp coerceToTimestamp(Object value) {
        if (value == null) return null;
        if (value instanceof Timestamp) return (Timestamp) value;
        if (value instanceof java.util.Date) return new Timestamp(((java.util.Date) value).getTime());
        String s = String.valueOf(value).trim();
        // Try ISO-like formats supported by SQLite/JDBC
        try { return Timestamp.valueOf(s.replace('T', ' ')); } catch (Exception ignore) {}
        try { return Timestamp.from(Instant.parse(s)); } catch (Exception ignore) {}
        return null;
    }

    private void syncUsers() {
        String table = "Users";
        Timestamp last = getLastSyncedAt(table);
        List<Map<String, Object>> rows = sqlite.queryForList(
                "SELECT username, email, password_hash, created_at, updated_at FROM Users WHERE updated_at > ? ORDER BY updated_at",
                last
        );

        Timestamp maxTs = last;
        int success = 0, failed = 0;
        for (Map<String, Object> r : rows) {
            oracle.update(
                // Upsert by unique email
                "MERGE INTO Users t USING (SELECT ? AS email FROM dual) s ON (t.email = s.email) " +
                "WHEN MATCHED THEN UPDATE SET t.username = ?, t.password_hash = ?, t.updated_at = ? " +
                "WHEN NOT MATCHED THEN INSERT (username, email, password_hash, created_at, updated_at) VALUES(?, ?, ?, ?, ?)",
                r.get("email"),
                r.get("username"), r.get("password_hash"), r.get("updated_at"),
                r.get("username"), r.get("email"), r.get("password_hash"), r.get("created_at"), r.get("updated_at")
            );
            success++;
            Timestamp rowTs = coerceToTimestamp(r.get("updated_at"));
            if (rowTs != null && (maxTs == null || rowTs.after(maxTs))) maxTs = rowTs;
        }
        if (success > 0 && maxTs != null) updateLastSyncedAt(table, maxTs);
        log.info("Users sync done: success={} failed={}", success, failed);
    }

    private void syncCategories() {
        String table = "Categories";
        Timestamp last = getLastSyncedAt(table);
        List<Map<String, Object>> rows = sqlite.queryForList(
                "SELECT category_name, category_type, description, created_at FROM Categories WHERE created_at > ? ORDER BY created_at",
                last
        );

        Timestamp maxTs = last;
        int success = 0, failed = 0;
        for (Map<String, Object> r : rows) {
            oracle.update(
                // Upsert by unique category_name
                "MERGE INTO Categories t USING (SELECT ? AS category_name FROM dual) s ON (t.category_name = s.category_name) " +
                "WHEN MATCHED THEN UPDATE SET t.category_type = ?, t.description = ? " +
                "WHEN NOT MATCHED THEN INSERT (category_name, category_type, description) VALUES(?, ?, ?)",
                r.get("category_name"),
                r.get("category_type"), r.get("description"),
                r.get("category_name"), r.get("category_type"), r.get("description")
            );
            success++;
            Timestamp rowTs = coerceToTimestamp(r.get("created_at"));
            if (rowTs != null && (maxTs == null || rowTs.after(maxTs))) maxTs = rowTs;
        }
        if (success > 0 && maxTs != null) updateLastSyncedAt(table, maxTs);
        log.info("Categories sync done: success={} failed={}", success, failed);
    }

    private void syncTransactions() {
        String table = "Transactions";
        Timestamp last = getLastSyncedAt(table);
        List<Map<String, Object>> rows = sqlite.queryForList(
                "SELECT user_id, category_id, amount, transaction_type, transaction_date, description, payment_method, created_at, updated_at FROM Transactions WHERE created_at > ? ORDER BY created_at",
                last
        );

        Timestamp maxTs = last;
        int success = 0, failed = 0, skippedFk = 0;
        for (Map<String, Object> r : rows) {
            try {
                Map<String, Object> srcUser = sqlite.queryForMap("SELECT email FROM Users WHERE user_id = ?", r.get("user_id"));
                Map<String, Object> srcCat = sqlite.queryForMap("SELECT category_name FROM Categories WHERE category_id = ?", r.get("category_id"));

                List<Long> userIds = oracle.query("SELECT user_id FROM Users WHERE email = ?", (rs, rn) -> rs.getLong(1), srcUser.get("email"));
                List<Long> catIds = oracle.query("SELECT category_id FROM Categories WHERE category_name = ?", (rs, rn) -> rs.getLong(1), srcCat.get("category_name"));
                if (userIds.isEmpty() || catIds.isEmpty()) {
                    skippedFk++;
                    log.warn("Skipping transaction due to missing FK mapping: email={}, category={}", srcUser.get("email"), srcCat.get("category_name"));
                } else {
                    Long targetUserId = userIds.get(0);
                    Long targetCategoryId = catIds.get(0);
                    oracle.update(
                        "INSERT INTO Transactions (user_id, category_id, amount, transaction_type, transaction_date, description, payment_method, created_at, updated_at) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        targetUserId, targetCategoryId, r.get("amount"), r.get("transaction_type"), r.get("transaction_date"), r.get("description"), r.get("payment_method"), r.get("created_at"), r.get("updated_at")
                    );
                    success++;
                }
            } catch (Exception e) {
                failed++;
                log.error("Failed to insert transaction: {}", r, e);
            }
            Timestamp rowTs = coerceToTimestamp(r.get("created_at"));
            if (rowTs != null && (maxTs == null || rowTs.after(maxTs))) maxTs = rowTs;
        }

        if (success > 0 && maxTs != null) updateLastSyncedAt(table, maxTs);
        log.info("Transactions sync done: success={} failed={} skippedFk={}", success, failed, skippedFk);
    }

    private void syncBudgets() {
        String table = "Budgets";
        Timestamp last = getLastSyncedAt(table);
        List<Map<String, Object>> rows = sqlite.queryForList(
                "SELECT user_id, category_id, budget_amount, budget_period, start_date, end_date, created_at, updated_at FROM Budgets WHERE created_at > ? ORDER BY created_at",
                last
        );

        Timestamp maxTs = last;
        int success = 0, failed = 0, skippedFk = 0;
        for (Map<String, Object> r : rows) {
            try {
                Map<String, Object> srcUser = sqlite.queryForMap("SELECT email FROM Users WHERE user_id = ?", r.get("user_id"));
                Map<String, Object> srcCat = sqlite.queryForMap("SELECT category_name FROM Categories WHERE category_id = ?", r.get("category_id"));
                List<Long> userIds = oracle.query("SELECT user_id FROM Users WHERE email = ?", (rs, rn) -> rs.getLong(1), srcUser.get("email"));
                List<Long> catIds = oracle.query("SELECT category_id FROM Categories WHERE category_name = ?", (rs, rn) -> rs.getLong(1), srcCat.get("category_name"));
                if (userIds.isEmpty() || catIds.isEmpty()) {
                    skippedFk++;
                    log.warn("Skipping budget due to missing FK mapping: email={}, category={}", srcUser.get("email"), srcCat.get("category_name"));
                } else {
                    Long targetUserId = userIds.get(0);
                    Long targetCategoryId = catIds.get(0);
                    oracle.update(
                        "INSERT INTO Budgets (user_id, category_id, budget_amount, budget_period, start_date, end_date, created_at, updated_at) VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                        targetUserId, targetCategoryId, r.get("budget_amount"), r.get("budget_period"), r.get("start_date"), r.get("end_date"), r.get("created_at"), r.get("updated_at")
                    );
                    success++;
                }
            } catch (Exception e) {
                failed++;
                log.error("Failed to insert budget: {}", r, e);
            }
            Timestamp rowTs = coerceToTimestamp(r.get("created_at"));
            if (rowTs != null && (maxTs == null || rowTs.after(maxTs))) maxTs = rowTs;
        }

        if (success > 0 && maxTs != null) updateLastSyncedAt(table, maxTs);
        log.info("Budgets sync done: success={} failed={} skippedFk={}", success, failed, skippedFk);
    }

    private void syncSavingsGoals() {
        String table = "SavingsGoals";
        Timestamp last = getLastSyncedAt(table);
        List<Map<String, Object>> rows = sqlite.queryForList(
                "SELECT user_id, goal_name, target_amount, current_amount, target_date, created_at, updated_at, status FROM SavingsGoals WHERE created_at > ? ORDER BY created_at",
                last
        );

        Timestamp maxTs = last;
        int success = 0, failed = 0, skippedFk = 0;
        for (Map<String, Object> r : rows) {
            try {
                Map<String, Object> srcUser = sqlite.queryForMap("SELECT email FROM Users WHERE user_id = ?", r.get("user_id"));
                List<Long> userIds = oracle.query("SELECT user_id FROM Users WHERE email = ?", (rs, rn) -> rs.getLong(1), srcUser.get("email"));
                if (userIds.isEmpty()) {
                    skippedFk++;
                    log.warn("Skipping savings goal due to missing user mapping: email={}", srcUser.get("email"));
                } else {
                    Long targetUserId = userIds.get(0);
                    oracle.update(
                        "INSERT INTO SavingsGoals (user_id, goal_name, target_amount, current_amount, target_date, created_at, updated_at, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                        targetUserId, r.get("goal_name"), r.get("target_amount"), r.get("current_amount"), r.get("target_date"), r.get("created_at"), r.get("updated_at"), r.get("status")
                    );
                    success++;
                }
            } catch (Exception e) {
                failed++;
                log.error("Failed to insert savings goal: {}", r, e);
            }
            Timestamp rowTs = coerceToTimestamp(r.get("created_at"));
            if (rowTs != null && (maxTs == null || rowTs.after(maxTs))) maxTs = rowTs;
        }

        if (success > 0 && maxTs != null) updateLastSyncedAt(table, maxTs);
        log.info("SavingsGoals sync done: success={} failed={} skippedFk={}", success, failed, skippedFk);
    }
}


