package com.example.User_Service.config;

import com.example.User_Service.service.UserSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncScheduler {

    @Autowired
    private UserSyncService syncService;

    // Automatically runs every 30 seconds
    @Scheduled(fixedRate = 5000)
    public void scheduleSync() {
        System.out.println("⏳ [Scheduler] User sync process started...");

        try {
            syncService.syncUsers();

            System.out.println("✅ [Scheduler] User sync process completed.");
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] User sync failed: " + e.getMessage());
        }
    }
}
