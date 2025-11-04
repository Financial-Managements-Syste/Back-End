package com.example.category_service.repository.oracle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.category_service.entity.oracle.OracleCategory;

@Repository
public interface OracleCategoryRepository extends JpaRepository<OracleCategory, Integer> {

    @Procedure(name = "insertCategoryFromSQLite")
    void insertCategoryFromSQLite(
            @Param("p_category_name") String categoryName,
            @Param("p_category_type") String categoryType,
            @Param("p_description") String description,
            @Param("p_created_at") java.sql.Timestamp createdAt
    );

    @Procedure(name = "updateCategoryFromSQLite")
    void updateCategoryFromSQLite(
            @Param("p_category_id") Integer categoryId,
            @Param("p_category_name") String categoryName,
            @Param("p_category_type") String categoryType,
            @Param("p_description") String description
    );

    @Procedure(name = "deleteCategoryFromSQLite")
    void deleteCategoryFromSQLite(
            @Param("p_category_id") Integer categoryId
    );
}
