package com.example.User_Service.repository.oracle;

import com.example.User_Service.entity.oracle.OracleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OracleUserRepository extends JpaRepository<OracleUser, Integer> {

    Optional<OracleUser> findByEmail(String email);

    Optional<OracleUser> findByUsername(String username);
}
