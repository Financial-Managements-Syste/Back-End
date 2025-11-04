package com.example.budget_service.repository.oracle;

import com.example.budget_service.entity.oracle.OracleBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OracleBudgetRepository extends JpaRepository<OracleBudget, Long> {

    // ✅ Insert
    @Procedure(procedureName = "insert_budget_from_sqlite")
    void insertBudgetFromSQLite(
            @Param("p_user_id") Long userId,
            @Param("p_category_id") Long categoryId,
            @Param("p_budget_name") String budgetName,
            @Param("p_budget_description") String budgetDescription,
            @Param("p_budget_amount") Double budgetAmount,
            @Param("p_budget_period") String budgetPeriod,
            @Param("p_start_date") java.sql.Date startDate,
            @Param("p_end_date") java.sql.Date endDate,
            @Param("p_created_at") java.sql.Timestamp createdAt,
            @Param("p_updated_at") java.sql.Timestamp updatedAt
    );

    // ✅ Update
    @Procedure(procedureName = "update_budget_from_sqlite")
    void updateBudgetFromSQLite(
            @Param("p_budget_id") Long budgetId,
            @Param("p_user_id") Long userId,
            @Param("p_category_id") Long categoryId,
            @Param("p_budget_name") String budgetName,
            @Param("p_budget_description") String budgetDescription,
            @Param("p_budget_amount") Double budgetAmount,
            @Param("p_budget_period") String budgetPeriod,
            @Param("p_start_date") java.sql.Date startDate,
            @Param("p_end_date") java.sql.Date endDate,
            @Param("p_updated_at") java.sql.Timestamp updatedAt
    );

    // ✅ Delete
    @Procedure(procedureName = "delete_budget_from_sqlite")
    void deleteBudgetFromSQLite(
            @Param("p_budget_id") Long budgetId
    );
}
