package com.creativedrive.user.config;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Bootstrap MongoDB integration.
 */
@Configuration
@ComponentScan
@EnableMongoRepositories(basePackages = {"com.creativedrive.user.persistence"})
public class MongoDBConfig extends AbstractMongoConfiguration {

    @Autowired
    private MongoProperties properties;

    private MongoClient client;

    @Override
    public MongoClient mongoClient() {
        if (client == null) {
            client = new MongoClient(properties.getHost(), properties.getPort());
        }
        return client;
    }

    @Override
    protected String getDatabaseName() {
        return properties.getDatabase();
    }


    /**
     * Factory for database migration runner
     *
     * @param environment {@link org.springframework.core.env.Environment}
     * @return {@link com.github.mongobee.Mongobee}
     */
    @Bean
    public Mongobee mongobee(Environment environment) {
        Mongobee runner = new Mongobee(mongoClient());
        runner.setDbName(properties.getDatabase());
        // Define where to look for changelogs
        runner.setChangeLogsScanPackage("com.creativedrive.user.persistence.changelogs");
        // Pass environment in order to track profiles
        runner.setSpringEnvironment(environment);
        return runner;
    }
}
