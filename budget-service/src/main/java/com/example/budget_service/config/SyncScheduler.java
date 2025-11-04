package com.example.budget_service.config;

import com.example.budget_service.service.BudgetSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncScheduler {

    @Autowired
    private BudgetSyncService syncService;

    // Automatically runs every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void scheduleSync() {
        System.out.println("⏳ [Scheduler] Budget sync process started...");

        try {
            syncService.syncBudgets();

            System.out.println("✅ [Scheduler] Budget sync process completed.");
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] Budget sync failed: " + e.getMessage());
        }
    }
}
