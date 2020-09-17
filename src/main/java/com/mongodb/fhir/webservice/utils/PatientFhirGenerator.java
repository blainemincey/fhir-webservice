package com.mongodb.fhir.webservice.utils;

import com.mongodb.fhir.webservice.model.Patient;
import com.mongodb.fhir.webservice.model.Condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

/**
 * Class used to generate random FHIR Patient Resources with random number and types
 * of conditions.  The serialized FHIR strings are then POSTed to our FHIR Server.
 */
public class PatientFhirGenerator {

    // number of patients to generate
    // defaults to 100
    private int numPatients = 100;

    private static HttpHeaders headers;
    private static RestTemplate restTemplate;

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(PatientFhirGenerator.class);

    /**
     *
     */
    public PatientFhirGenerator(int numPatients) {

        if(numPatients > 0) {
            log.info("Setting new number of patients to generate.");
            this.numPatients = numPatients;
        }

        log.info("Number of Patients to generate: " + this.numPatients);

        // create auth credentials
        // these values are hard-coded for our FHIR server.  If you modify the values in the FHIR server
        // application.properties file, you must change them here if you use this class to generate data.
        String authStr = "fhirUser:fhirUserPassword";
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        // create headers
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate = new RestTemplate();

        this.generatePatients();
    }

    /**
     * Generate random FHIR Patient/Condition resources.
     */
    private void generatePatients() {

        for(int idx = 0; idx < numPatients; idx++) {

            // get random patient object with random number/types of conditions
            Patient patient = RandomDataGenerator.getPatient();
            String serializedPatient = FhirSerializer.serializePatient(patient);

            // post to FHIR server
            this.postPatientToWebService(serializedPatient);

            // serialize the conditions
            if(patient.getConditions().size() > 0) {
                for(Condition condition : patient.getConditions() ) {
                    String serializedCondition = FhirSerializer.serializeCondition(condition, patient.getPatientId());
                    this.postConditionToWebService(serializedCondition);
                }
            }
        }
    }

    /**
     * Post serialized FHIR Patient resource to FHIR server
     *
     * @param fhirPatient
     */
    private void postPatientToWebService(String fhirPatient) {
        HttpEntity<String> request = new HttpEntity<String>(fhirPatient, headers);
        ResponseEntity<String> response
                = new RestTemplate().exchange("http://localhost:8090/processFhir/patient", HttpMethod.POST, request, String.class);
        if(response != null) {
            log.info(response.getBody());
        } else {
            log.info("Null response.");
        }

    }

    /**
     * Post serialized FHIR Condition resource to FHIR server
     *
     * @param fhirCondition
     */
    private void postConditionToWebService(String fhirCondition) {
        HttpEntity<String> request = new HttpEntity<String>(fhirCondition, headers);
        ResponseEntity<String> response
                = new RestTemplate().exchange("http://localhost:8090/processFhir/condition", HttpMethod.POST, request, String.class);
        if(response != null) {
            log.info(response.getBody());
        } else {
            log.info("Null response.");
        }
    }

    /**
     * Main method to drive patient generation
     * Or, this class can be invoked from a REST endpoint
     *
     * @param args
     */
    public static void main(String[] args) {

        // generate 100 patients
        new PatientFhirGenerator(100);
    }
}
