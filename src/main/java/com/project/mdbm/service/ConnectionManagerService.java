package com.project.mdbm.service;

import com.project.mdbm.config.DataSourceConfig;
import com.project.mdbm.config.DataSourceContextHolder;
import com.project.mdbm.config.DynamicMongoManager;
import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.entity.DBDetails;
import com.project.mdbm.repository.DBDetailsRepository;
import com.project.mdbm.utils.ResponseUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ConnectionManagerService {

    private static final String SESSION_CONNECTION_NAME = "connectionName";
    private static final String SESSION_DB_TYPE = "dbType";
    private static final String NON_RELATIONAL = "Non-Relational";

    @Lazy
    @Autowired
    private DBDetailsRepository dbDetailsRepository;

    @Lazy
    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private DynamicMongoManager dynamicMongoManager;

    @Autowired
    private DataSource dataSource;

    public GenericAPIResponse connectToDatabase(String connectionName, HttpSession session) {
        GenericAPIResponse genericAPIResponse = ResponseUtils.getResponseObject();

        DBDetails dbDetails = dbDetailsRepository.findByConnectionName(connectionName);

        if (dbDetails == null) {
            genericAPIResponse.setCode(400);
            genericAPIResponse.setMessage("Database not found with name: " + connectionName);
            return genericAPIResponse;
        }

        try {
            if (dbDetails.getDbType().equalsIgnoreCase("Relational")) {
                dataSourceConfig.addDataSource(connectionName, dbDetails);
                DataSourceContextHolder.setDataSourceKey(connectionName);
            } else if (dbDetails.getDbType().equalsIgnoreCase("Non-Relational")) {
                dynamicMongoManager.addMongoTemplate(connectionName, dbDetails);
            }

            session.setAttribute(SESSION_CONNECTION_NAME, connectionName);
            session.setAttribute(SESSION_DB_TYPE, dbDetails.getDbType());

            genericAPIResponse.setCode(200);
            genericAPIResponse.setMessage("Successfully connected to " + connectionName);
            return genericAPIResponse;
        } catch (Exception e) {
            genericAPIResponse.setCode(500);
            genericAPIResponse.setMessage("Failed to connect to database: " + e.getMessage());
            return genericAPIResponse;
        }
    }

    public List<String> getTables(HttpSession session) {
        String connectionName = (String) session.getAttribute(SESSION_CONNECTION_NAME);
        String dbType = (String) session.getAttribute(SESSION_DB_TYPE);

        List<String> tables = new ArrayList<>();

        try {
            if (!StringUtils.hasText(connectionName) || !StringUtils.hasText(dbType)) {
                return tables;
            }

            if (NON_RELATIONAL.equalsIgnoreCase(dbType)) {
                MongoTemplate mongoTemplate = dynamicMongoManager.getMongoTemplate(connectionName);
                tables.addAll(mongoTemplate.getCollectionNames());
                tables.sort(String::compareToIgnoreCase);
                return tables;
            }

            DataSourceContextHolder.setDataSourceKey(connectionName);

            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();

                try (ResultSet rs = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String tableName = rs.getString("TABLE_NAME");
                        if (StringUtils.hasText(tableName)) {
                            tables.add(tableName);
                        }
                    }
                }
            }

            tables.sort(Comparator.comparing(String::toLowerCase));
            return tables;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch tables: " + e.getMessage(), e);
        } finally {
            DataSourceContextHolder.clear();
        }
    }

    private String extractDatabaseName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}