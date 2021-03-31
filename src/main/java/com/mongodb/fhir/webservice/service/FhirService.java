package com.mongodb.fhir.webservice.service;

import com.mongodb.fhir.webservice.db.FhirMongoOperations;
import com.mongodb.fhir.webservice.model.Condition;
import com.mongodb.fhir.webservice.model.ConditionEvent;
import com.mongodb.fhir.webservice.model.EventStream;
import com.mongodb.fhir.webservice.model.Patient;
import com.mongodb.fhir.webservice.parser.ConditionParser;
import com.mongodb.fhir.webservice.parser.PatientParser;
import com.mongodb.fhir.webservice.utils.FhirSerializer;
import org.springframework.data.mongodb.core.MongoOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Service class to process all FHIR Resources
 */
@Component
public class FhirService {

    private FhirMongoOperations fhirMongoOperations;

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(FhirService.class);

    /**
     *
     * @param fhirMongoOperations
     */
    public FhirService(FhirMongoOperations fhirMongoOperations) {
        this.fhirMongoOperations = fhirMongoOperations;
    }

    /**
     * Process FHIR Patient resource.
     *
     * @param patientResource
     * @return Patient Model class
     */
    public Patient processPatient(String patientResource){
        log.info("Process Fhir patient.");
        Patient patient = PatientParser.parse(patientResource);

        // uses encrypted client
        MongoOperations mongoOps = fhirMongoOperations.getEncMongoOperations();
        mongoOps.insert(patient);

        return patient;
    }

    /**
     * Process FHIR Condition resource.
     *
     * @param conditionResource
     * @return Patient model class
     */
    public Patient processCondition(String conditionResource){
        log.info("Process Fhir condition.");

        // Since we parse condition separately and do not store with the condition object,
        // we return a condition object and the subject id separately
        Object[] conditionResults = ConditionParser.parse(conditionResource);
        Condition condition = (Condition)conditionResults[0];
        String subjectReference = (String)conditionResults[1];

        // build criteria query
        Query query = new Query();
        query.addCriteria(Criteria.where("patientId").is(subjectReference));

        // add condition to the object array
        Update update = new Update();
        update.addToSet("conditions", condition);
        update.set("updateDate", new java.util.Date());

        // save it with encrypted client
        MongoOperations mongoOps = fhirMongoOperations.getEncMongoOperations();
        mongoOps.findAndModify(query, update, Patient.class);

        // Return the updated patient
        Patient patient = mongoOps.findOne(query, Patient.class);

        // Pass elements to our event stream for reporting
        this.processConditionEvent(patient, condition);

        return patient;
    }

    /**
     * Filter for patient model by patient id.
     *
     * @param patientId
     * @return patient model
     */
    public Patient processPatientId(String patientId){
        log.info("Process Patient Id for id: " + patientId);

        Query query = new Query();
        query.addCriteria(Criteria.where("patientId").is(patientId));

        // Since patient id uses Client side field level encryption, get the encrypted connection
        // If we try to filter a CS-FLE field without the encrypted client, the find will return 'null'
        MongoOperations mongoOps = fhirMongoOperations.getEncMongoOperations();
        Patient patient = mongoOps.findOne(query,Patient.class);

        return patient;
    }

    /**
     * Insert a conditionevent object as our event stream for reporting.
     *
     * @param patient
     * @param condition
     */
    private void processConditionEvent(Patient patient, Condition condition) {
        if(patient != null && condition != null) {
            log.info("Process Condition Event.");
            ConditionEvent conditionEvent = new ConditionEvent();
            conditionEvent.setBirthdate(patient.getBirthDate());
            conditionEvent.setCity(patient.getCity());
            conditionEvent.setState(patient.getState());
            conditionEvent.setGender(patient.getGender());
            conditionEvent.setCondition(condition.getCondition());
            conditionEvent.setConditionCode(condition.getConditionCode());
            conditionEvent.setOnsetDate(condition.getOnsetDate());
            conditionEvent.setReportedDate(new java.util.Date());

            // save it
            // get un-encrypted client, no fields to encrypt as we are sending to reporting site
            MongoOperations mongoOperations = fhirMongoOperations.getMongoOperations();
            mongoOperations.insert(conditionEvent);

        } else {
            log.error("Invalid patient and/or condition for ConditionEvent.");
        }
    }

    /**
     * Simple example of a separate event stream to log all interactions with
     * FHIR Server.
     *
     * @param eventStream
     */
    public void processEventStream(EventStream eventStream){
        log.info("Process Event Stream.");

        // Demo purposes - get the un-encrypted client to show raw incoming event
        MongoOperations mongoOps = fhirMongoOperations.getMongoOperations();
        mongoOps.insert(eventStream);

        log.info(eventStream.toString());
    }

    /**
     * Method to be used to generate a Bundle Resource and return to client.
     *
     * @param patientId
     * @return
     */
    public String processBundle(String patientId) {
        log.info("Process Bundle for patientId: " + patientId);

        String bundleResourceAsString = "";

        Patient patient = this.processPatientId(patientId);
        if(patient != null) {
            bundleResourceAsString = FhirSerializer.serializeBundle(patient);

            log.info("Bundle result: " + bundleResourceAsString);
        }

        return bundleResourceAsString;
    }
}
