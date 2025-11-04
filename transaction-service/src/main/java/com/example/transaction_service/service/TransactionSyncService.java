package com.example.transaction_service.service;

import com.example.transaction_service.entity.sqlite.SQLiteTransaction;
import com.example.transaction_service.repository.oracle.OracleTransactionRepository;
import com.example.transaction_service.repository.sqlite.SQLiteTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionSyncService {

    @Autowired
    private SQLiteTransactionRepository sqliteRepo;

    @Autowired
    private OracleTransactionRepository oracleRepo;

    @Autowired
    @Qualifier("oracleTransactionManager")
    private PlatformTransactionManager oracleTransactionManager;

    private TransactionTemplate oracleTransactionTemplate;

    @PostConstruct
    public void init() {
        this.oracleTransactionTemplate = new TransactionTemplate(oracleTransactionManager);
    }

    // --- MASTER SYNC METHOD ---
    public void syncTransactions() {
        System.out.println("⏳ [Sync] Starting full transaction sync process...");

        try {
            syncInserts();
            syncUpdates();
            syncDeletes();
        } catch (Exception e) {
            System.err.println("❌ [Sync] Transaction sync failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✅ [Sync] Full transaction sync completed successfully.");
    }

    // --- INSERT SYNC ---
    private void syncInserts() {
        List<SQLiteTransaction> newTxs = sqliteRepo.findBySyncStatus("NEW");

        for (SQLiteTransaction tx : newTxs) {
            try {
                // Execute Oracle operation within transaction
                oracleTransactionTemplate.execute(status -> {
                    oracleRepo.insertTransactionFromSQLite(
                            (long) tx.getUserId(),
                            (long) tx.getCategoryId(),
                            tx.getAmount(),
                            tx.getTransactionType(),
                            Date.valueOf(LocalDate.parse(tx.getTransactionDate())),
                            tx.getDescription(),
                            tx.getPaymentMethod(),
                            Timestamp.valueOf(LocalDateTime.now())
                    );
                    return null;
                });

                // Update SQLite (uses its own transaction manager)
                tx.setIsSynced(1);
                tx.setSyncStatus("SYNCED");
                sqliteRepo.save(tx);

                System.out.println("✅ [Insert Sync] Synced tx ID: " + tx.getTransactionId());
            } catch (Exception e) {
                System.err.println("❌ [Insert Sync] Failed for tx ID " + tx.getTransactionId() + ": " + e.getMessage());
            }
        }
    }

    // --- UPDATE SYNC ---
    private void syncUpdates() {
        List<SQLiteTransaction> updatedTxs = sqliteRepo.findBySyncStatus("UPDATED");

        for (SQLiteTransaction tx : updatedTxs) {
            try {
                // Execute Oracle operation within transaction
                oracleTransactionTemplate.execute(status -> {
                    oracleRepo.updateTransactionFromSQLite(
                            (long) tx.getTransactionId(),
                            (long) tx.getUserId(),
                            (long) tx.getCategoryId(),
                            tx.getAmount(),
                            tx.getTransactionType(),
                            Date.valueOf(LocalDate.parse(tx.getTransactionDate())),
                            tx.getDescription(),
                            tx.getPaymentMethod()
                    );
                    return null;
                });

                // Update SQLite (uses its own transaction manager)
                tx.setIsSynced(1);
                tx.setSyncStatus("SYNCED");
                sqliteRepo.save(tx);

                System.out.println("✅ [Update Sync] Synced tx ID: " + tx.getTransactionId());
            } catch (Exception e) {
                System.err.println("❌ [Update Sync] Failed for tx ID " + tx.getTransactionId() + ": " + e.getMessage());
            }
        }
    }

    // --- DELETE SYNC ---
    private void syncDeletes() {
        List<SQLiteTransaction> deletedTxs = sqliteRepo.findBySyncStatus("DELETED");

        for (SQLiteTransaction tx : deletedTxs) {
            try {
                // Execute Oracle operation within transaction - delete by matching fields
                oracleTransactionTemplate.execute(status -> {
                    oracleRepo.deleteTransactionFromSQLite(
                            (long) tx.getUserId(),
                            (long) tx.getCategoryId(),
                            tx.getAmount(),
                            tx.getTransactionType(),
                            Date.valueOf(LocalDate.parse(tx.getTransactionDate())),
                            tx.getDescription(),
                            tx.getPaymentMethod()
                    );
                    return null;
                });

                // Delete from SQLite only after successful Oracle deletion
                sqliteRepo.delete(tx);

                System.out.println("🗑️ [Delete Sync] Deleted tx ID: " + tx.getTransactionId());
            } catch (Exception e) {
                System.err.println("❌ [Delete Sync] Failed for tx ID " + tx.getTransactionId() + ": " + e.getMessage());
            }
        }
    }
}
