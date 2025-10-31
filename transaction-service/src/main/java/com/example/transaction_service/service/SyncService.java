package com.example.transaction_service.service;

import com.example.transaction_service.entity.sqlite.SQLiteTransaction;
import com.example.transaction_service.entity.oracle.OracleTransaction;
import com.example.transaction_service.repository.oracle.OracleTransactionRepository;
import com.example.transaction_service.repository.sqlite.SQLiteTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    @Autowired
    private SQLiteTransactionRepository sqliteRepo;

    @Autowired
    private OracleTransactionRepository oracleRepo;

    @Transactional(transactionManager = "oracleTransactionManager")
    public void syncTransactions() {
        // 1. Fetch all SQLite transactions
        List<SQLiteTransaction> sqliteTransactions = sqliteRepo.findAll();
		int inserted = 0;
		int updated = 0;
		int deleted = 0;
		int failed = 0;

		// Build a set of SQLite IDs for delete detection
		java.util.Set<Long> sqliteIds = new java.util.HashSet<>();
		for (SQLiteTransaction sqliteTx : sqliteTransactions) {
			sqliteIds.add((long) sqliteTx.getTransactionId());
			boolean exists = oracleRepo.existsById((long) sqliteTx.getTransactionId());
			if (!exists) {
				OracleTransaction oracleTx = new OracleTransaction();
				oracleTx.setTransactionId((long) sqliteTx.getTransactionId());
				oracleTx.setUserId((long) sqliteTx.getUserId());
				oracleTx.setCategoryId((long) sqliteTx.getCategoryId());
				oracleTx.setAmount(sqliteTx.getAmount());
				oracleTx.setTransactionType(sqliteTx.getTransactionType());
                try {
					oracleTx.setTransactionDate(LocalDate.parse(sqliteTx.getTransactionDate()));
				} catch (Exception e) {
					oracleTx.setTransactionDate(LocalDate.now());
				}
				oracleTx.setDescription(sqliteTx.getDescription());
				oracleTx.setPaymentMethod(sqliteTx.getPaymentMethod());
				try {
					oracleRepo.save(oracleTx);
					inserted++;
				} catch (Exception ex) {
					failed++;
					log.error("Failed to insert tx id={} into Oracle: {}", sqliteTx.getTransactionId(), ex.getMessage());
				}
			} else {
				// Update existing Oracle row to mirror SQLite
				try {
					OracleTransaction oracleExisting = oracleRepo.findById((long) sqliteTx.getTransactionId()).orElse(null);
                    if (oracleExisting != null) {
						oracleExisting.setUserId((long) sqliteTx.getUserId());
						oracleExisting.setCategoryId((long) sqliteTx.getCategoryId());
						oracleExisting.setAmount(sqliteTx.getAmount());
						oracleExisting.setTransactionType(sqliteTx.getTransactionType());
						try {
							oracleExisting.setTransactionDate(LocalDate.parse(sqliteTx.getTransactionDate()));
						} catch (Exception e) {
							oracleExisting.setTransactionDate(LocalDate.now());
						}
						oracleExisting.setDescription(sqliteTx.getDescription());
						oracleExisting.setPaymentMethod(sqliteTx.getPaymentMethod());
                        // bump updated_at on update
                        oracleExisting.setUpdatedAt(LocalDateTime.now());
						oracleRepo.save(oracleExisting);
						updated++;
					}
				} catch (Exception ex) {
					failed++;
					log.error("Failed to update tx id={} in Oracle: {}", sqliteTx.getTransactionId(), ex.getMessage());
				}
			}
		}

		// Delete Oracle rows that no longer exist in SQLite
		try {
			List<OracleTransaction> oracleAll = oracleRepo.findAll();
			for (OracleTransaction ot : oracleAll) {
				Long id = ot.getTransactionId();
				if (!sqliteIds.contains(id)) {
					try {
						oracleRepo.deleteById(id);
						deleted++;
					} catch (Exception ex) {
						failed++;
						log.error("Failed to delete tx id={} from Oracle: {}", id, ex.getMessage());
					}
				}
			}
		} catch (Exception ex) {
			log.error("Delete phase failed: {}", ex.getMessage());
		}

		log.info("Sync completed. SQLite: {}. Inserted: {}. Updated: {}. Deleted: {}. Failed ops: {}.", sqliteTransactions.size(), inserted, updated, deleted, failed);
    }
}
