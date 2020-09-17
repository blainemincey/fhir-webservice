package com.mongodb.fhir.webservice.db;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Configure the Mongo client
 */
@Configuration
public class MongoClientConfiguration extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value(("${spring.data.mongodb.collection}"))
    private String collection;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    //
    // Encrypted properties
    //
    @Value("${mongodb.key.database}")
    private String keyDb;

    @Value("${mongodb.key.collection}")
    private String keyCollection;

    @Value("${mongodb.key.name}")
    private String keyName;

    @Value("${mongodb.key.kmsProvider}")
    private String keyProvider;

    @Value("${mongodb.cryptdpath}")
    private String cryptdpath;

    @Value("${mongodb.masterKeyPath}")
    private String masterKeyPath;

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(MongoClientConfiguration.class);

    /**
     * Returns 'normal' client without encryption
     * If this is desired, change the appropriate method calls in FhirService
     *
     * @return
     */
    @Override
    public MongoClient mongoClient() {

        return MongoClients.create(mongoUri);
    }

    /**
     *
     * @return
     */
    @Override
    public String getDatabaseName() {
        return database;
    }

    /**
     * Returns the encrypted client for use with Client-Side Field Level Encryption
     * @return
     */
    public MongoClient encryptedMongoClient() {
        log.info("Create encrypted mongo client");
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(mongoUri))
                .autoEncryptionSettings(getEncryptedMongoClientSettings())
                .build();

        return MongoClients.create(clientSettings);
    }

    /**
     * Helper method to build AutoEncryptionSettings
     *
     * @return
     */
    public AutoEncryptionSettings getEncryptedMongoClientSettings() {
        // Helper class with majority of encryption methods
        CSFLEHelpers helper = new CSFLEHelpers();

        byte[] masterKeyBytes = new byte[0];
        String recordsNamespace = database + "." + collection;
        String keyVaultCollection = String.join(".", keyDb, keyCollection);
        try {
            log.info("Read master key file");
            masterKeyBytes = helper.readMasterKey(masterKeyPath);

        } catch (Exception e) {
            log.error("Exception reading master key: " + e);
        }

        String encryptionKey = helper.findDataEncryptionKey(mongoUri,
                keyName,
                keyDb,
                keyCollection);

        if (encryptionKey == null && masterKeyBytes.length > 0) {
            // No key found; create index on key vault and a new encryption key and print the key
            CSFLEHelpers.createKeyVaultIndex(mongoUri, keyDb, keyVaultCollection);
            encryptionKey = CSFLEHelpers.createDataEncryptionKey(  mongoUri,
                    keyProvider,
                    masterKeyBytes,
                    keyVaultCollection,
                    keyName);

            log.info("Created new encryption key: " + encryptionKey);
        } else {
            // Print the key
            log.info("Found existing encryption key: " + encryptionKey);
        }

        Document schema = CSFLEHelpers.createJSONSchema(encryptionKey);

        Map<String, BsonDocument> schemaMap = new HashMap<>();
        schemaMap.put(recordsNamespace, BsonDocument.parse(schema.toJson()));

        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", masterKeyBytes);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<>();
        kmsProviders.put(keyProvider, keyMap);

        Map<String, Object> extraOpts = new HashMap<>();
        extraOpts.put("mongocryptdSpawnPath", cryptdpath);

        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                .keyVaultNamespace(keyVaultCollection)
                .kmsProviders(kmsProviders)
                .extraOptions(extraOpts)
                .schemaMap(schemaMap)
                .build();

        return autoEncryptionSettings;
    }
}
