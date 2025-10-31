package com.example.sync.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.sqlite")
    public DataSourceProperties sqliteDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "sqliteDataSource")
    public DataSource sqliteDataSource() {
        return sqliteDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @ConfigurationProperties("app.datasource.oracle")
    public DataSourceProperties oracleDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "oracleDataSource")
    @Primary
    public DataSource oracleDataSource() {
        return oracleDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "sqliteJdbcTemplate")
    public JdbcTemplate sqliteJdbcTemplate(DataSource sqliteDataSource) {
        return new JdbcTemplate(sqliteDataSource);
    }

    @Bean(name = "oracleJdbcTemplate")
    public JdbcTemplate oracleJdbcTemplate(DataSource oracleDataSource) {
        return new JdbcTemplate(oracleDataSource);
    }
}


