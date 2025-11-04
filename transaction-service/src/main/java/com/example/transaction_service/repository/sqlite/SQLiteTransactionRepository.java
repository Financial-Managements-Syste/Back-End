package com.example.transaction_service.repository.sqlite;

import com.example.transaction_service.entity.sqlite.SQLiteTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLiteTransactionRepository extends JpaRepository<SQLiteTransaction, Integer> {

    // Fetch all transactions of a specific user
    List<SQLiteTransaction> findByUserId(Integer userId);

    // Fetch all transactions of a specific category
    List<SQLiteTransaction> findByCategoryId(Integer categoryId);

    // Fetch transactions that are not yet synced (for old code compatibility)
    List<SQLiteTransaction> findByIsSyncedFalse();

    // ✅ New methods for sync service

    // Fetch transactions ready for insert
    List<SQLiteTransaction> findBySyncStatus(String syncStatus);

    // Fetch deleted transactions (optional)
    List<SQLiteTransaction> findByIsDeletedAndSyncStatus(int isDeleted, String syncStatus);
}
