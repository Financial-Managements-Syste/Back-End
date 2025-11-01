package com.example.category_service.repository.sqlite;

import com.example.category_service.entity.sqlite.SQLiteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLiteCategoryRepository extends JpaRepository<SQLiteCategory, Integer> {
    List<SQLiteCategory> findByIsSyncedFalse();
}

