package com.example.savving_service.config;

import com.example.savving_service.service.SavingsGoalSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncScheduler {

    @Autowired
    private SavingsGoalSyncService syncService;

    // Automatically runs every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void scheduleSync() {
        System.out.println("⏳ [Scheduler] SavingsGoal sync process started...");

        try {
            syncService.syncSavingsGoals();
            System.out.println("✅ [Scheduler] SavingsGoal sync process completed.");
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] SavingsGoal sync failed: " + e.getMessage());
        }
    }
}
