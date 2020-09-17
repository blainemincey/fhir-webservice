package com.mongodb.fhir.webservice.db;

import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.*;

/**
 * Class file developed by MongoDB University w/
 * minor edits by Blaine Mincey.
 *
 * Helper methods and sample data for this companion project.
 */
public class CSFLEHelpers {

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(CSFLEHelpers.class);

    /**
     * Reads the 96-byte local master key
     * Reads as resource to keep in line with maven
     *
     * @param resourceFile
     * @return
     * @throws Exception
     */
    public byte[] readMasterKey(String resourceFile) throws Exception {
        int numBytes = 96;
        byte[] fileBytes = new byte[numBytes];

        // uses recent java11 fis.readNBytes
        try (FileInputStream fis = new FileInputStream(resourceFile)) {
            fis.readNBytes(fileBytes, 0, numBytes);
        } catch (Exception e) {
            log.error("Read Master Key Exception: " + e);
            fileBytes = null;
        }

        return fileBytes;
    }

    /**
     * JSON Schema Helpers
     *
     * @param bsonType
     * @param isDeterministic
     * @return
     */
    private static Document buildEncryptedField(String bsonType, Boolean isDeterministic) {
        String DETERMINISTIC_ENCRYPTION_TYPE = "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic";
        String RANDOM_ENCRYPTION_TYPE = "AEAD_AES_256_CBC_HMAC_SHA_512-Random";

        return new Document().
                append("encrypt", new Document()
                        .append("bsonType", bsonType)
                        .append("algorithm",
                                (isDeterministic) ? DETERMINISTIC_ENCRYPTION_TYPE : RANDOM_ENCRYPTION_TYPE));
    }

    /**
     *
     * @param keyId
     * @return
     */
    private static Document createEncryptMetadataSchema(String keyId) {
        List<Document> keyIds = new ArrayList<>();
        keyIds.add(new Document()
                .append("$binary", new Document()
                        .append("base64", keyId)
                        .append("subType", "04")));
        return new Document().append("keyId", keyIds);
    }

    /**
     * This method encrypts specific fields from the Patient Model class.
     * Specifically, patientId, firstName, lastName, address, and postalCode.
     *
     * @param keyId
     * @return
     * @throws IllegalArgumentException
     */
    public static Document createJSONSchema(String keyId) throws IllegalArgumentException {
        if (keyId.isEmpty()) {
            throw new IllegalArgumentException("keyId must contain your base64 encryption key id.");
        }
        return new Document().append("bsonType", "object")
                                .append("encryptMetadata", createEncryptMetadataSchema(keyId))
                                .append("properties", new Document()
                                .append("patientId", buildEncryptedField("string", true))
                                .append("firstName", buildEncryptedField("string", false))
                                .append("lastName", buildEncryptedField("string", false))
                                .append("address", buildEncryptedField("string", false))
                                .append("postalCode", buildEncryptedField("string", false)));


    }

    /**
     * Creates a 'normal' non-encrypted Mongo client.  This method is ONLY
     * used internally by this specific class in order to persist encyryption key
     * in MongoDB.
     *
     * @param connectionString
     * @return
     */
    private static MongoClient createMongoClient(String connectionString) {
        return MongoClients.create(connectionString);
    }

    /**
     * Creates KeyVault which allows you to create a key as well as encrypt and decrypt fields
     *
     * @param connectionString
     * @param kmsProvider
     * @param localMasterKey
     * @param keyVaultCollection
     * @return
     */
    private static ClientEncryption createKeyVault(String connectionString, String kmsProvider,
                                                   byte[] localMasterKey, String keyVaultCollection) {
        Map<String, Object> masterKeyMap = new HashMap<>();
        masterKeyMap.put("key", localMasterKey);
        Map<String, Map<String, Object>> kmsProviders = new HashMap<>();
        kmsProviders.put(kmsProvider, masterKeyMap);

        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build())
                .keyVaultNamespace(keyVaultCollection)
                .kmsProviders(kmsProviders)
                .build();

        return ClientEncryptions.create(clientEncryptionSettings);
    }


    /**
     * Returns existing encryption key if it exists.
     *
     * @param connectionString
     * @param keyAltName
     * @param keyDb
     * @param keyColl
     * @return
     */
    public static String findDataEncryptionKey(String connectionString, String keyAltName, String keyDb, String keyColl) {
        try (MongoClient mongoClient = createMongoClient(connectionString)) {
            Document query = new Document("keyAltNames", keyAltName);
            MongoCollection<Document> collection = mongoClient.getDatabase(keyDb).getCollection(keyColl);
            BsonDocument doc = collection
                    .withDocumentClass(BsonDocument.class)
                    .find(query)
                    .first();

            if (doc != null) {
                return Base64.getEncoder().encodeToString(doc.getBinary("_id").getData());
            }
            return null;
        }
    }

    /**
     * Creates index for keyAltNames in the specified key collection
     *
     * @param connectionString
     * @param keyDb
     * @param keyColl
     */
    public static void createKeyVaultIndex(String connectionString, String keyDb, String keyColl) {
        try (MongoClient mongoClient = createMongoClient(connectionString)) {
            MongoCollection<Document> collection = mongoClient.getDatabase(keyDb).getCollection(keyColl);

            Bson filterExpr = Filters.exists("keyAltNames", true);
            IndexOptions indexOptions = new IndexOptions().unique(true).partialFilterExpression(filterExpr);

            collection.createIndex(new Document("keyAltNames", 1), indexOptions);
        }
    }

    /**
     * Create data encryption key in the specified key collection
     * Call only after checking whether a data encryption key with same keyAltName exists
     *
     * @param connectionString
     * @param kmsProvider
     * @param localMasterKey
     * @param keyVaultCollection
     * @param keyAltName
     * @return
     */
    public static String createDataEncryptionKey(String connectionString, String kmsProvider,
                                                 byte[] localMasterKey, String keyVaultCollection, String keyAltName) {

        List<String> keyAltNames = new ArrayList<>();
        keyAltNames.add(keyAltName);

        try (ClientEncryption keyVault = createKeyVault(connectionString, kmsProvider, localMasterKey, keyVaultCollection)) {
            BsonBinary dataKeyId = keyVault.createDataKey(kmsProvider, new DataKeyOptions().keyAltNames(keyAltNames));

            return Base64.getEncoder().encodeToString(dataKeyId.getData());
        }
    }
}
