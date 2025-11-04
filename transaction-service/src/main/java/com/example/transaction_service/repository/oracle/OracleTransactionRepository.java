package com.example.transaction_service.repository.oracle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.transaction_service.entity.oracle.OracleTransaction;

@Repository
public interface OracleTransactionRepository extends JpaRepository<OracleTransaction, Long> {

    @Modifying
    @Query(value = "{call insert_transaction_from_sqlite(:p_user_id, :p_category_id, :p_amount, :p_transaction_type, :p_transaction_date, :p_description, :p_payment_method, :p_created_at)}", nativeQuery = true)
    void insertTransactionFromSQLite(
            @Param("p_user_id") Long userId,
            @Param("p_category_id") Long categoryId,
            @Param("p_amount") Double amount,
            @Param("p_transaction_type") String transactionType,
            @Param("p_transaction_date") java.sql.Date transactionDate,
            @Param("p_description") String description,
            @Param("p_payment_method") String paymentMethod,
            @Param("p_created_at") java.sql.Timestamp createdAt
    );

    @Modifying
    @Query(value = "{call update_transaction_from_sqlite(:p_transaction_id, :p_user_id, :p_category_id, :p_amount, :p_transaction_type, :p_transaction_date, :p_description, :p_payment_method)}", nativeQuery = true)
    void updateTransactionFromSQLite(
            @Param("p_transaction_id") Long transactionId,
            @Param("p_user_id") Long userId,
            @Param("p_category_id") Long categoryId,
            @Param("p_amount") Double amount,
            @Param("p_transaction_type") String transactionType,
            @Param("p_transaction_date") java.sql.Date transactionDate,
            @Param("p_description") String description,
            @Param("p_payment_method") String paymentMethod
    );

    @Modifying
    @Query(value = "{call delete_transaction_from_sqlite(:p_user_id, :p_category_id, :p_amount, :p_transaction_type, :p_transaction_date, :p_description, :p_payment_method)}", nativeQuery = true)
    void deleteTransactionFromSQLite(
            @Param("p_user_id") Long userId,
            @Param("p_category_id") Long categoryId,
            @Param("p_amount") Double amount,
            @Param("p_transaction_type") String transactionType,
            @Param("p_transaction_date") java.sql.Date transactionDate,
            @Param("p_description") String description,
            @Param("p_payment_method") String paymentMethod
    );
}
