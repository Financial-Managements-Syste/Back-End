package com.example.category_service.config;

import com.example.category_service.service.CategorySyncService;
import com.example.category_service.service.CategorySyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
@ConditionalOnClass(name = "oracle.jdbc.OracleDriver")
@ConditionalOnBean(CategorySyncService.class)
public class SyncScheduler {

    @Autowired
    private CategorySyncService syncService;

    // Every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void scheduleSync() {
        syncService.syncCategories();
    }
}
