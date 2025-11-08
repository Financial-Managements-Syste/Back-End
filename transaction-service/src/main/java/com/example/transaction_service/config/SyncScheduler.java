package com.example.transaction_service.config;

import com.example.transaction_service.service.TransactionSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncScheduler {

    @Autowired
    private TransactionSyncService syncService;

    // Automatically runs every 30 seconds
    @Scheduled(fixedRate = 5000)
    public void scheduleSync() {
        System.out.println("⏳ [Scheduler] Transaction sync process started...");

        try {
            syncService.syncTransactions();
            System.out.println("✅ [Scheduler] Transaction sync process completed.");
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] Transaction sync failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
