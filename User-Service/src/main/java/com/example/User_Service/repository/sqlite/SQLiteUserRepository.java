package com.example.User_Service.repository.sqlite;

import com.example.User_Service.entity.sqlite.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLiteUserRepository extends JpaRepository<User, Integer> {

    // Corrected: match entity property name `isSynced` (camelCase)
    List<User> findByIsSynced(int isSynced);

    // No change needed, matches `username` property
    User findByUsername(String username);

    User findByEmail(String email);


}
