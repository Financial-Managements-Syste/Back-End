package com.example.savving_service.repository.oracle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.savving_service.entity.oracle.OracleSavingsGoal;

@Repository
public interface OracleSavingsGoalRepository extends JpaRepository<OracleSavingsGoal, Long> {

    // Insert the Record
    @Procedure(name = "insertSavingFromSQLite")
    void insertSavingFromSQLite(
            @Param("p_goal_id") Long goalId,
            @Param("p_user_id") Long userId,
            @Param("p_goal_name") String goalName,
            @Param("p_target_amount") Double targetAmount,
            @Param("p_current_amount") Double currentAmount,
            @Param("p_target_date") java.sql.Date targetDate,
            @Param("p_created_at") java.sql.Timestamp createdAt,
            @Param("p_updated_at") java.sql.Timestamp updatedAt,
            @Param("p_status") String status
    );

    // Updte and exsisting record
    @Procedure(name = "updateSavingFromSQLite")
    void updateSavingFromSQLite(
            @Param("p_goal_id") Long goalId,
            @Param("p_user_id") Long userId,
            @Param("p_goal_name") String goalName,
            @Param("p_target_amount") Double targetAmount,
            @Param("p_current_amount") Double currentAmount,
            @Param("p_target_date") java.sql.Date targetDate,
            @Param("p_updated_at") java.sql.Timestamp updatedAt,
            @Param("p_status") String status
    );

    // Deletes a record in Oracle corresponding to a SQLite record
    @Procedure(name = "deleteSavingFromSQLite")
    void deleteSavingFromSQLite(@Param("p_goal_id") Long goalId);
}
