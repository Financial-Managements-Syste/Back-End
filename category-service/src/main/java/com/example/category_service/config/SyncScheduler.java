package com.example.category_service.config;

import com.example.category_service.service.CategorySyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncScheduler {

    @Autowired
    private CategorySyncService syncService;

    // Automatically runs every 30 seconds
    @Scheduled(fixedRate = 5000)
    public void scheduleSync() {
        System.out.println("⏳ [Scheduler] Category sync process started...");

        try {
            syncService.syncCategories();

            System.out.println("✅ [Scheduler] Category sync process completed.");
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] Category sync failed: " + e.getMessage());
        }
    }
}
