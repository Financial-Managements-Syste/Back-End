package com.example.category_service.repository.sqlite;

import com.example.category_service.entity.sqlite.SQLiteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLiteCategoryRepository extends JpaRepository<SQLiteCategory, Integer> {

    // Fetch all records by sync status (NEW, UPDATED, DELETED)
    List<SQLiteCategory> findBySyncStatus(String syncStatus);

    // Optional: Fetch all unsynced records (is_synced = 0)
    List<SQLiteCategory> findByIsSynced(int isSynced);

    // Optional: Fetch all deleted but not yet synced categories
    List<SQLiteCategory> findByIsDeletedAndSyncStatus(int isDeleted, String syncStatus);
}
