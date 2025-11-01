package com.example.transaction_service.repository.sqlite;

import com.example.transaction_service.entity.sqlite.SQLiteTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLiteTransactionRepository extends JpaRepository<SQLiteTransaction, Integer> {
    List<SQLiteTransaction> findByUserId(Integer userId);
    List<SQLiteTransaction> findByCategoryId(Integer categoryId);

    List<SQLiteTransaction> findByIsSyncedFalse(); // ✅ for syncing only unsynced records
}
