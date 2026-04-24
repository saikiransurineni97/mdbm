package com.project.mdbm.config;

import com.project.mdbm.entity.DBDetails;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;
// import java.util.HashMap;   // REMOVED
import java.util.concurrent.ConcurrentHashMap; // ADDED

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.project.mdbm.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@EnableMongoRepositories(basePackages = "com.project.mdbm.repository.mongo")
public class DataSourceConfig {

    @Value("${spring.datasource.mysql.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.mysql.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.mysql.password}")
    private String mysqlPassword;

    @Value("${spring.datasource.mysql.driver-class-name}")
    private String mysqlDriverClassName;

    private DynamicDataSource dynamicDataSource;

    private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>(); // ADDED

    @Bean
    public DataSource dataSource() {
        dynamicDataSource = new DynamicDataSource();

        DataSource mysqlDataSource = createMySQLDataSource();

        // Map<Object, Object> targetDataSources = new HashMap<>();   // REMOVED
        targetDataSources.put("mysql", mysqlDataSource); // MODIFIED

        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(mysqlDataSource);
        dynamicDataSource.afterPropertiesSet(); // ADDED

        return dynamicDataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource())
                .packages("com.project.mdbm.entity")
                .persistenceUnit("dynamic")
                .build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    public void addDataSource(String key, DBDetails dbDetails) {

        if (targetDataSources.containsKey(key)) { // ADDED
            return; // ADDED
        } // ADDED

        String driverClassName;

        String url = dbDetails.getUrl();

        if (url.contains("postgresql")) {
            driverClassName = "org.postgresql.Driver";
        } else if (url.contains("mysql")) {
            driverClassName = "com.mysql.cj.jdbc.Driver";
        } else {
            throw new RuntimeException("Unsupported database url: " + url);
        }

        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.url(url);
        builder.username(dbDetails.getUserName());
        builder.password(dbDetails.getPassword());
        builder.driverClassName(driverClassName);

        // Map<Object, Object> targetDataSources = new HashMap<>();   // REMOVED
        // targetDataSources.put("mysql", createMySQLDataSource());   // REMOVED
        targetDataSources.put(key, builder.build()); // MODIFIED

        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.afterPropertiesSet();
    }

    private DataSource createMySQLDataSource() {
        return DataSourceBuilder.create()
                .url(mysqlUrl)
                .username(mysqlUsername)
                .password(mysqlPassword)
                .driverClassName(mysqlDriverClassName)
                .build();
    }
}