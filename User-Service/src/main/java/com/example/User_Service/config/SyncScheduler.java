package com.example.User_Service.config;

import com.example.User_Service.service.UserSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
@ConditionalOnClass(name = "oracle.jdbc.OracleDriver")
@ConditionalOnBean(UserSyncService.class)
public class SyncScheduler {

    @Autowired
    private UserSyncService syncService;

    // Every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void scheduleSync() {
        syncService.syncUsers();
    }
}
