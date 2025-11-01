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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncService {

	private static final Logger log = LoggerFactory.getLogger(SyncService.class);

	@Autowired
	private SQLiteTransactionRepository sqliteRepo;

	@Autowired
	private OracleTransactionRepository oracleRepo;

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Transactional(transactionManager = "oracleTransactionManager")
	public void syncTransactions() {
		List<SQLiteTransaction> sqliteTransactions = sqliteRepo.findAll();
		List<OracleTransaction> oracleTransactions = oracleRepo.findAll();

		int inserted = 0, updated = 0, deleted = 0, failed = 0;

		Map<String, OracleTransaction> oracleMap = new HashMap<>();
		for (OracleTransaction ot : oracleTransactions) {
			String key = generateKey(ot.getUserId(), ot.getCategoryId(), ot.getTransactionDate());
			oracleMap.put(key, ot);
		}

		// --- INSERT / UPDATE ---
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
					// INSERT
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

					// mark as synced in SQLite
					st.setIsSynced(1);
					sqliteRepo.save(st);
					inserted++;
				} else {
					// UPDATE
					boolean changed = false;
					if (!ot.getAmount().equals(st.getAmount())) {
						ot.setAmount(st.getAmount());
						changed = true;
					}
					if (!ot.getTransactionType().equals(st.getTransactionType())) {
						ot.setTransactionType(st.getTransactionType());
						changed = true;
					}
					if (!Objects.equals(ot.getDescription(), st.getDescription())) {
						ot.setDescription(st.getDescription());
						changed = true;
					}
					if (!Objects.equals(ot.getPaymentMethod(), st.getPaymentMethod())) {
						ot.setPaymentMethod(st.getPaymentMethod());
						changed = true;
					}

					if (changed) {
						ot.setUpdatedAt(LocalDateTime.now());
						oracleRepo.save(ot);
					}

					// mark as synced in SQLite even if no change
					st.setIsSynced(1);
					sqliteRepo.save(st);
					updated++;
				}
			} catch (Exception ex) {
				failed++;
				log.error("Failed to sync SQLite tx={} to Oracle: {}", st.getTransactionId(), ex.getMessage());
			}
		}

		// --- DELETE Oracle transactions not in SQLite ---
		Set<String> sqliteKeys = new HashSet<>();
		for (SQLiteTransaction st : sqliteTransactions) {
			LocalDate txDate;
			try {
				txDate = LocalDate.parse(st.getTransactionDate(), formatter);
			} catch (Exception e) {
				txDate = LocalDate.now();
			}
			sqliteKeys.add(generateKey((long) st.getUserId(), (long) st.getCategoryId(), txDate));
		}

		for (OracleTransaction ot : oracleTransactions) {
			String key = generateKey(ot.getUserId(), ot.getCategoryId(), ot.getTransactionDate());
			if (!sqliteKeys.contains(key)) {
				try {
					oracleRepo.delete(ot);
					deleted++;
				} catch (Exception e) {
					failed++;
					log.error("Failed to delete Oracle tx={} : {}", ot.getTransactionId(), e.getMessage());
				}
			}
		}

		log.info("Sync completed. Inserted: {}. Updated: {}. Deleted: {}. Failed: {}.", inserted, updated, deleted, failed);
	}

	private String generateKey(Long userId, Long categoryId, LocalDate date) {
		return userId + "_" + categoryId + "_" + date.toString();
	}
}
