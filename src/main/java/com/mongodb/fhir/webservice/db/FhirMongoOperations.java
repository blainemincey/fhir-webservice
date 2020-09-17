package com.mongodb.fhir.webservice.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.stereotype.Component;

/**
 * Abstraction to interface with Mongo Client Configuration.
 */
@Component
public class FhirMongoOperations {

    // Logger component
    private static final Logger logger = LoggerFactory.getLogger(FhirMongoOperations.class);

    private MongoClientConfiguration mongoClientConfiguration;

    private MongoOperations mongoOps;

    private MongoOperations encMongoOps;

    /**
     *
     * @param mongoClientConfiguration
     */
    public FhirMongoOperations(MongoClientConfiguration mongoClientConfiguration) {
        this.mongoClientConfiguration = mongoClientConfiguration;
    }

    /**
     * Get the normal client
     *
     * @return
     */
    public MongoOperations getMongoOperations() {

        if(mongoOps != null) {

            logger.info("Return existing mongoOps.");

        } else {

            logger.info("Create new mongoOps.");

            mongoOps =
                    new MongoTemplate(new SimpleMongoClientDbFactory(mongoClientConfiguration.mongoClient(),
                            mongoClientConfiguration.getDatabaseName()));
        }
        return mongoOps;
    }

    /**
     * Get the ENCRYPTED client
     *
     * @return
     */
    public MongoOperations getEncMongoOperations() {

        if(encMongoOps != null) {

            logger.info("Return existing ENCRYPTED mongoOps.");

        } else {

            logger.info("Create new ENCRYPTED mongoOps.");

            encMongoOps =
                    new MongoTemplate(new SimpleMongoClientDbFactory(mongoClientConfiguration.encryptedMongoClient(),
                            mongoClientConfiguration.getDatabaseName()));
        }
        return encMongoOps;
    }
}
