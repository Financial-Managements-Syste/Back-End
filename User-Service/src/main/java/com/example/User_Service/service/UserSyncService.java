package com.example.User_Service.service;

import com.example.User_Service.entity.sqlite.User;
import com.example.User_Service.entity.oracle.OracleUser;
import com.example.User_Service.repository.oracle.OracleUserRepository;
import com.example.User_Service.repository.sqlite.SQLiteUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@EnableScheduling
public class UserSyncService {

    @Autowired
    private SQLiteUserRepository sqliteRepo;

    @Autowired
    private OracleUserRepository oracleRepo;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void syncUsers() {
        // Fetch unsynced SQLite users
        List<User> unsyncedUsers = sqliteRepo.findByIsSynced(0);

        System.out.println("🔄 Found " + unsyncedUsers.size() + " unsynced users in SQLite.");

        for (User sqliteUser : unsyncedUsers) {
            // Check if user already exists in Oracle by email or username
            Optional<OracleUser> existingUser = oracleRepo.findByEmail(sqliteUser.getEmail());
            if (existingUser.isEmpty()) {
                existingUser = oracleRepo.findByUsername(sqliteUser.getUsername());
            }

            if (existingUser.isPresent()) {
                System.out.println("⚠️ User already exists in Oracle: " + sqliteUser.getUsername());
            } else {
                // Map SQLite user to Oracle user
                OracleUser oracleUser = new OracleUser();
                oracleUser.setUsername(sqliteUser.getUsername());
                oracleUser.setEmail(sqliteUser.getEmail());
                oracleUser.setPasswordHash(sqliteUser.getPassword_hash());

                // Use default ISO parsing for LocalDateTime
                LocalDateTime createdAt = sqliteUser.getCreated_at() != null ?
                        LocalDateTime.parse(sqliteUser.getCreated_at()) : LocalDateTime.now();
                oracleUser.setCreatedAt(createdAt);

                LocalDateTime updatedAt = sqliteUser.getUpdated_at() != null ?
                        LocalDateTime.parse(sqliteUser.getUpdated_at()) : createdAt;
                oracleUser.setUpdatedAt(updatedAt);

                oracleRepo.save(oracleUser);
                System.out.println("➕ Inserted user into Oracle: " + sqliteUser.getUsername());
            }

            // Mark SQLite user as synced
            sqliteUser.setIsSynced(1);
            sqliteRepo.save(sqliteUser);
        }

        System.out.println("✅ User sync completed (inserts only).");
    }

}
