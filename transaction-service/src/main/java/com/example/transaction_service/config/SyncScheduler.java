package com.example.transaction_service.config;

import com.example.transaction_service.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncScheduler {

    @Autowired
    private SyncService syncService;

    // Every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void scheduleSync() {
        syncService.syncTransactions();
    }
}
