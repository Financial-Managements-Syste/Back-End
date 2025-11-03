package com.example.transaction_service.service;

import com.example.transaction_service.entity.sqlite.SQLiteTransaction;
import com.example.transaction_service.entity.oracle.OracleTransaction;
import com.example.transaction_service.repository.oracle.OracleTransactionRepository;
import com.example.transaction_service.repository.sqlite.SQLiteTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SyncService: syncs SQLite transactions into Oracle safely.
 * Each Oracle operation runs in its own transaction to prevent global rollback.
 */
@Service
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncService {

	private static final Logger log = LoggerFactory.getLogger(SyncService.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Autowired private SQLiteTransactionRepository sqliteRepo;
	@Autowired private OracleTransactionRepository oracleRepo;

	public void syncTransactions() {
		log.info("Starting transaction sync...");

		List<SQLiteTransaction> sqliteTransactions = sqliteRepo.findAll();
		List<OracleTransaction> oracleTransactions = oracleRepo.findAll();

		Map<String, OracleTransaction> oracleMap = new HashMap<>();
		for (OracleTransaction ot : oracleTransactions) {
			oracleMap.put(generateKey(ot.getUserId(), ot.getCategoryId(), ot.getTransactionDate()), ot);
		}

		int inserted = 0, updated = 0, deleted = 0, failed = 0;

		// --- INSERT / UPDATE phase ---
		for (SQLiteTransaction st : sqliteTransactions) {
			LocalDate txDate;
			try {
				txDate = LocalDate.parse(st.getTransactionDate(), formatter);
			} catch (Exception e) {
				txDate = LocalDate.now();
			}

			String key = generateKey((long) st.getUserId(), (long) st.getCategoryId(), txDate);
			OracleTransaction ot = oracleMap.get(key);

			try {
				if (ot == null) {
					// Save new record in its own transaction
					saveNewOracleTransaction(st, txDate);
					markSQLiteAsSynced(st);
					inserted++;
				} else {
					// Update in its own transaction
					updateOracleTransaction(ot, st);
					markSQLiteAsSynced(st);
					updated++;
				}
			} catch (Exception e) {
				failed++;
				log.error("❌ Failed to sync SQLite tx={} : {}", st.getTransactionId(), e.getMessage());
			}
		}

		// --- DELETE phase ---
		Set<String> sqliteKeys = buildSQLiteKeySet(sqliteTransactions);
		for (OracleTransaction ot : oracleTransactions) {
			String key = generateKey(ot.getUserId(), ot.getCategoryId(), ot.getTransactionDate());
			if (!sqliteKeys.contains(key)) {
				try {
					deleteOracleTransaction(ot);
					deleted++;
				} catch (Exception e) {
					failed++;
					log.error("❌ Failed to delete Oracle tx={} : {}", ot.getTransactionId(), e.getMessage());
				}
			}
		}

		log.info("✅ Sync done. Inserted: {} | Updated: {} | Deleted: {} | Failed: {}",
				inserted, updated, deleted, failed);
	}

	/** Runs in its own independent Oracle transaction */
	@Transactional(transactionManager = "oracleTransactionManager", propagation = Propagation.REQUIRES_NEW)
	public void saveNewOracleTransaction(SQLiteTransaction st, LocalDate txDate) {
		OracleTransaction newTx = new OracleTransaction();
		newTx.setUserId((long) st.getUserId());
		newTx.setCategoryId((long) st.getCategoryId());
		newTx.setAmount(st.getAmount());
		newTx.setTransactionType(st.getTransactionType());
		newTx.setTransactionDate(txDate);
		newTx.setDescription(st.getDescription());
		newTx.setPaymentMethod(st.getPaymentMethod());
		newTx.setCreatedAt(LocalDateTime.now());
		newTx.setUpdatedAt(LocalDateTime.now());
		oracleRepo.save(newTx);
	}

	/** Runs in its own independent Oracle transaction */
	@Transactional(transactionManager = "oracleTransactionManager", propagation = Propagation.REQUIRES_NEW)
	public void updateOracleTransaction(OracleTransaction ot, SQLiteTransaction st) {
		boolean changed = false;
		if (!ot.getAmount().equals(st.getAmount())) { ot.setAmount(st.getAmount()); changed = true; }
		if (!ot.getTransactionType().equals(st.getTransactionType())) { ot.setTransactionType(st.getTransactionType()); changed = true; }
		if (!Objects.equals(ot.getDescription(), st.getDescription())) { ot.setDescription(st.getDescription()); changed = true; }
		if (!Objects.equals(ot.getPaymentMethod(), st.getPaymentMethod())) { ot.setPaymentMethod(st.getPaymentMethod()); changed = true; }

		if (changed) {
			ot.setUpdatedAt(LocalDateTime.now());
			oracleRepo.save(ot);
		}
	}

	/** Runs in its own independent Oracle transaction */
	@Transactional(transactionManager = "oracleTransactionManager", propagation = Propagation.REQUIRES_NEW)
	public void deleteOracleTransaction(OracleTransaction ot) {
		oracleRepo.delete(ot);
	}

	private void markSQLiteAsSynced(SQLiteTransaction st) {
		try {
			st.setIsSynced(1);
			sqliteRepo.save(st);
		} catch (Exception e) {
			log.warn("⚠ Failed to mark SQLite tx={} as synced: {}", st.getTransactionId(), e.getMessage());
		}
	}

	private Set<String> buildSQLiteKeySet(List<SQLiteTransaction> sqliteTransactions) {
		Set<String> keys = new HashSet<>();
		for (SQLiteTransaction st : sqliteTransactions) {
			LocalDate txDate;
			try {
				txDate = LocalDate.parse(st.getTransactionDate(), formatter);
			} catch (Exception e) {
				txDate = LocalDate.now();
			}
			keys.add(generateKey((long) st.getUserId(), (long) st.getCategoryId(), txDate));
		}
		return keys;
	}

	private String generateKey(Long userId, Long categoryId, LocalDate date) {
		return userId + "_" + categoryId + "_" + date;
	}
}
