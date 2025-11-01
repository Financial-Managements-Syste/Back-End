package com.example.category_service.repository.oracle;

import com.example.category_service.entity.oracle.OracleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OracleCategoryRepository extends JpaRepository<OracleCategory, Integer> {
    Optional<OracleCategory> findByCategoryName(String categoryName);
    boolean existsByCategoryName(String categoryName);
}
