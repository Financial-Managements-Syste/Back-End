package com.example.budget_service.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
@ConditionalOnClass(name = "oracle.jdbc.OracleDriver")
@EnableJpaRepositories(
        basePackages = "com.example.budget_service.repository.oracle",
        entityManagerFactoryRef = "oracleEntityManagerFactory",
        transactionManagerRef = "oracleTransactionManager"
)
public class OracleDataSourceConfig {

    @Bean
    @ConfigurationProperties("oracle.datasource")
    public DataSourceProperties oracleDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource oracleDataSource(@Qualifier("oracleDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactory(
            @Qualifier("oracleDataSource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        // ✅ scan budget entities
        emf.setPackagesToScan("com.example.budget_service.entity.oracle");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "none"); // avoid creating duplicate columns
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
        emf.setJpaPropertyMap(props);
        return emf;
    }

    @Bean
    public JpaTransactionManager oracleTransactionManager(
            @Qualifier("oracleEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
