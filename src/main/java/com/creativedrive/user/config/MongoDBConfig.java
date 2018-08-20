package com.creativedrive.user.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Bootstrap MongoDB integration.
 */
@Configuration
@EnableMongoRepositories
public class MongoDBConfig extends AbstractMongoConfiguration {

    @Autowired
    private  MongoProperties properties;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(properties.getHost(), properties.getPort());
    }

    @Override
    protected String getDatabaseName() {
        return properties.getDatabase();
    }
}
