package com.example.User_Service.repository.oracle;

import com.example.User_Service.entity.oracle.OracleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface OracleUserRepository extends JpaRepository<OracleUser, Long> {

    @Procedure(name = "insertUserFromSQLite")
    void insertUserFromSQLite(
            @Param("p_username") String username,
            @Param("p_email") String email,
            @Param("p_password_hash") String passwordHash,
            @Param("p_created_at") Timestamp createdAt,
            @Param("p_updated_at") Timestamp updatedAt
    );

    Optional<OracleUser> findByEmail(String email);
    Optional<OracleUser> findByUsername(String username);
}
