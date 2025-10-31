package com.example.transaction_service.controller;

import com.example.transaction_service.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin(origins = "*")
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class SyncController {

    @Autowired
    private SyncService syncService;

    @PostMapping("/run")
    public ResponseEntity<String> runSync() {
        syncService.syncTransactions();
        return ResponseEntity.ok("Sync triggered");
    }
}


