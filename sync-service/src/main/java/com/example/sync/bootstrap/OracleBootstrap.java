package com.example.sync.bootstrap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;

@Component
public class OracleBootstrap {

    private final JdbcTemplate oracleJdbcTemplate;

    public OracleBootstrap(@Qualifier("oracleJdbcTemplate") JdbcTemplate oracleJdbcTemplate) {
        this.oracleJdbcTemplate = oracleJdbcTemplate;
    }

    @PostConstruct
    public void ensureMetadataTable() {
        // Create Sync_Metadata table if not exists
        oracleJdbcTemplate.execute(
            "BEGIN \n" +
            "   EXECUTE IMMEDIATE 'CREATE TABLE Sync_Metadata (table_name VARCHAR2(50) PRIMARY KEY, last_synced_at TIMESTAMP)'; \n" +
            "EXCEPTION WHEN OTHERS THEN \n" +
            "   IF SQLCODE != -955 THEN RAISE; END IF; \n" + // -955: name is already used by an existing object
            "END;"
        );
    }
}


