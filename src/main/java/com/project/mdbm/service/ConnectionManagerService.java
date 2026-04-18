package com.project.mdbm.service;

import com.project.mdbm.config.*;
import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.entity.DBDetails;
import com.project.mdbm.repository.DBDetailsRepository;
import com.project.mdbm.utils.ResponseUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ConnectionManagerService {

    @Lazy
    @Autowired
    private DBDetailsRepository dbDetailsRepository;

    @Lazy
    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private DynamicMongoManager dynamicMongoManager;

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

            session.setAttribute("connectionName", connectionName);
            session.setAttribute("dbType", dbDetails.getDbType());

            genericAPIResponse.setCode(200);
            genericAPIResponse.setMessage("Successfully connected to " + connectionName);
            return genericAPIResponse;
        } catch (Exception e) {
            genericAPIResponse.setCode(500);
            genericAPIResponse.setMessage("Failed to connect to database: " + e.getMessage());
            return genericAPIResponse;
        }
    }

    private String extractDatabaseName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}