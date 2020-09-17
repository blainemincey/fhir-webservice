#!/bin/bash

#
# Example bash script to create MongoDB Kafka Source/Sink Connectors
#

# Modify the URI below to match that of your MongoDB Atlas Cluster
MONGODB_ATLAS_CONNECTION_URI="mongodb+srv://myAtlasUser:myAtlasUserPassword@myAtlasCluster"

echo "========================="
echo "Creating Kafka Connectors"
echo "========================="

echo -e "\nCreating sink-mongodb-influenza connector."
curl -X PUT "http://localhost:8083/connectors/sink-mongodb-influenza/config" -H "Content-Type: application/json" --data '
{
    "connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
    "tasks.max":"1",
    "topics":"fhirdata.influenza.fhirPatients.conditionEvent",
    "connection.uri":"'"$MONGODB_ATLAS_CONNECTION_URI"'",
    "database":"fhirReporting",
    "collection":"influenzaEvents",
    "key.converter":"org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable":false,
    "value.converter":"org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable":false
}' -w "\n"

sleep 2

echo -e "\nCreating source-fhir-influenza connector."
curl -X PUT "http://localhost:8083/connectors/source-fhir-influenza/config" -H "Content-Type: application/json" --data '
{
    "tasks.max":"1",
    "connector.class":"com.mongodb.kafka.connect.MongoSourceConnector",
    "key.converter":"org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable":false,
    "value.converter":"org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable":false,
    "publish.full.document.only": true,
    "connection.uri":"'"$MONGODB_ATLAS_CONNECTION_URI"'",
    "pipeline":"[{\"$match\": { \"$and\": [ { \"fullDocument.condition\" : \"Influenza\" }, {\"operationType\": \"insert\"}]}}]",
    "topic.prefix":"fhirdata.influenza",
    "database":"fhirPatients",
    "collection":"conditionEvent"
}' -w "\n"

sleep 2

echo -e "\nCreating sink-mongodb-reporting connector."
curl -X PUT "http://localhost:8083/connectors/sink-mongodb-reporting/config" -H "Content-Type: application/json" --data '
{
    "connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
    "tasks.max":"1",
    "topics":"fhirdata.fhirPatients.conditionEvent",
    "connection.uri":"'"$MONGODB_ATLAS_CONNECTION_URI"'",
    "database":"fhirReporting",
    "collection":"results",
    "key.converter":"org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable":false,
    "value.converter":"org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable":false
}' -w "\n"

sleep 2

echo -e "\nCreating source-fhir connector."
curl -X PUT "http://localhost:8083/connectors/source-fhir/config" -H "Content-Type: application/json" --data '
{
    "tasks.max":"1",
    "connector.class":"com.mongodb.kafka.connect.MongoSourceConnector",
    "key.converter":"org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable":false,
    "value.converter":"org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable":false,
    "publish.full.document.only": true,
    "connection.uri":"'"$MONGODB_ATLAS_CONNECTION_URI"'",
    "topic.prefix":"fhirdata",
    "database":"fhirPatients",
    "collection":"conditionEvent"
}' -w "\n"

sleep 2

echo -e "\n=========================="
echo "Verifying Kafka Connectors"
echo "=========================="

echo -e "\nKafka Connectors: \n"
curl -X GET "http://localhost:8083/connectors/" -w "\n"

echo -e "\n=== Complete ==="
