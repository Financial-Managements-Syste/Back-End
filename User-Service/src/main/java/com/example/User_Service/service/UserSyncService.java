package com.example.User_Service.service;

import com.example.User_Service.entity.sqlite.User;
import com.example.User_Service.repository.oracle.OracleUserRepository;
import com.example.User_Service.repository.sqlite.SQLiteUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserSyncService {
    
    @Autowired
    private SQLiteUserRepository sqliteRepo;

    @Autowired
    private OracleUserRepository oracleRepo;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LocalDateTime parseDateTime(String text) {
        if (text == null || text.isEmpty()) {
            return LocalDateTime.now(); // fallback to current time if missing
        }
        try {
            return LocalDateTime.parse(text, ISO_FORMATTER);
        } catch (Exception e) {
            // fallback to simple formatter
            return LocalDateTime.parse(text, SIMPLE_FORMATTER);
        }
    }

    public void syncUsers() {
        List<User> unsyncedUsers = sqliteRepo.findByIsSynced(0);

        for (User u : unsyncedUsers) {
            try {
                Timestamp createdAt = Timestamp.valueOf(parseDateTime(u.getCreated_at()));
                Timestamp updatedAt = Timestamp.valueOf(parseDateTime(u.getUpdated_at()));

                oracleRepo.insertUserFromSQLite(
                        u.getUsername(),
                        u.getEmail(),
                        u.getPassword_hash(),
                        createdAt,
                        updatedAt
                );

                u.setIsSynced(1);
                sqliteRepo.save(u);

                System.out.println("✅ Synced user: " + u.getUsername());
            } catch (Exception e) {
                System.err.println("❌ Failed to sync user: " + u.getUsername() + " -> " + e.getMessage());
            }
        }
    }


}
