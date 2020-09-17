package com.mongodb.fhir.webservice.service;

import com.google.gson.JsonObject;
import com.mongodb.fhir.webservice.model.EventStream;
import com.mongodb.fhir.webservice.model.Patient;
import com.mongodb.fhir.webservice.utils.PatientFhirGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ForkJoinPool;

/**
 * Spring Rest Controller - i.e. FHIR Server
 *
 * The request mapping matches that within the Spring Security configuration class.
 */
@RestController
@RequestMapping(path = "/processFhir")
public class MongoRestController {

    private FhirService fhirService;

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(MongoRestController.class);

    /**
     *
     * @param fhirService
     */
    public MongoRestController(FhirService fhirService) {
        this.fhirService = fhirService;
    }

    /**
     * Post FHIR Patient resource
     *
     * @param patient
     * @return
     */
    @PostMapping(value = "/patient", consumes = "application/json", produces = "application/json")
    public Patient postPatient(@RequestBody String patient) {
        log.info("Received Patient Fhir Message.");
        log.info(patient);

        // log event stream
        this.logEventStream(patient, EventStream.PATIENT_RESOURCE, EventStream.HTTP_POST);

        return this.fhirService.processPatient(patient);
    }

    /**
     * Post FHIR Condition resource
     *
     * @param condition
     * @return
     */
    @PostMapping(value = "/condition", consumes = "application/json", produces = "application/json")
    public Patient postCondition(@RequestBody String condition) {
        log.info("Received Condition Fhir Message.");
        log.info(condition);

        // log event stream
        this.logEventStream(condition, EventStream.CONDITION_RESOURCE, EventStream.HTTP_POST);

        return this.fhirService.processCondition(condition);
    }

    /**
     * Get patient by patient id
     *
     * @param patientId
     * @return
     */
    @GetMapping(value = "/getPatient/{patientId}", produces = "application/json")
    public Patient getPatient(@PathVariable String patientId) {
        log.info("Get Patient for Patient Id: " + patientId);

        // log event stream
        this.logEventStream(patientId, EventStream.PATIENT_ID_RESOURCE, EventStream.HTTP_GET);

        return this.fhirService.processPatientId(patientId);
    }

    /**
     * Log all events with unencrypted client
     *
     * @param fhirResourceString
     * @param fhirResourceType
     * @param eventMethod
     */
    private void logEventStream(String fhirResourceString, String fhirResourceType, String eventMethod) {
        log.info("Log Event Stream");

        EventStream eventStream = new EventStream();
        eventStream.setEventDate(new java.util.Date());
        eventStream.setRawEventString(fhirResourceString);
        eventStream.setEventMethod(eventMethod);
        eventStream.setResourceType(fhirResourceType);

        this.fhirService.processEventStream(eventStream);
    }

    /**
     * Helper method to generate random FHIR Patient data
     * Does NOT log itself to event stream
     *
     * Depending on number of patients requested, may timeout
     *
     * Possibly use deferredResult for more asynchronous behavior
     *
     * @param
     * @return
     */
    @PostMapping(value = "/generatePatientData/{numPatients}", produces = "text/plain")
    public String postGenerateFhirPatientData(@PathVariable int numPatients) {
        log.info("Request to generate patient data. Num of patients requested: " + numPatients);

        // call utility method to generate patients
        new PatientFhirGenerator(numPatients);

        return "Successfully generated number of patients: " + numPatients;
    }
}

