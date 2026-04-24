package com.project.mdbm.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.project.mdbm.entity.DBDetails;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicMongoManager {

    private final Map<String, MongoTemplate> mongoTemplateMap = new ConcurrentHashMap<>();

    //new Mongo DB Connection will be created with this method
    public void addMongoTemplate(String key, DBDetails dbDetails) {

        if (mongoTemplateMap.containsKey(key)) { // ADDED
            return; // ADDED
        } // ADDED

        String uri = dbDetails.getUrl();
        MongoClient mongoClient = MongoClients.create(uri);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, extractDatabaseName(uri));

        createIndexes(mongoTemplate); // MODIFIED

        mongoTemplateMap.put(key, mongoTemplate);
    }

    public MongoTemplate getMongoTemplate(String key) {
        MongoTemplate template = mongoTemplateMap.get(key);
        if (template == null) {
            throw new RuntimeException("No MongoDB connection found for: " + key);
        }
        return template;
    }

    private String extractDatabaseName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private void createIndexes(MongoTemplate mongoTemplate) { // MODIFIED
        mongoTemplate.indexOps("student")
                .createIndex(new Index().on("email", Sort.Direction.ASC).unique()); // MODIFIED

        mongoTemplate.indexOps("employee")
                .createIndex(new Index().on("email", Sort.Direction.ASC).unique()); // MODIFIED
    }
}