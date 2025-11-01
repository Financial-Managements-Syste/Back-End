package com.example.transaction_service.repository.oracle;

import com.example.transaction_service.entity.oracle.OracleTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OracleTransactionRepository extends JpaRepository<OracleTransaction, Long> {
    List<OracleTransaction> findByUserId(Long userId);
    List<OracleTransaction> findByCategoryId(Long categoryId);
    boolean existsByTransactionId(Long transactionId); // ✅ useful for sync checks
}
