package com.mongodb.fhir.webservice.parser;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.mongodb.fhir.webservice.model.Patient;
import org.hl7.fhir.r5.model.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for FHIR Patient Reference message.
 * Uses Hapi-Fhir libs.
 */
public class PatientParser {

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(PatientParser.class);

    /**
     *
     * @param patientResource
     */
    public static Patient parse(String patientResource) {

        Patient patient = null;

        if(patientResource != null && patientResource.length() > 1) {
            patient = parsePatient(patientResource);
        }

        return patient;
    }

    /**
     *
     * @param patientResource
     * @return
     */
    private static Patient parsePatient(String patientResource) {
        Patient patient = new Patient();

        // Create a FHIR context
        FhirContext ctx = FhirContext.forR5();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        // Parse it
        org.hl7.fhir.r5.model.Patient parsed = parser.parseResource(org.hl7.fhir.r5.model.Patient.class, patientResource);

        // Patient id - should be unique
        // removing the 'Patient/' prefix for demo purposes
        patient.setPatientId(parsed.getId().replace("Patient/", ""));

        // Name
        patient.setLastName(parsed.getName().get(0).getFamily());
        patient.setFirstName(parsed.getName().get(0).getGiven().get(0).getValueAsString());

        // Address
        Address address = parsed.getAddressFirstRep();
        patient.setAddress(address.getLine().get(0).toString());
        patient.setCity(address.getCity());
        patient.setState(address.getState());
        patient.setPostalCode(address.getPostalCode());

        // Gender/Birthday
        patient.setGender(parsed.getGender().toString());
        patient.setBirthDate(parsed.getBirthDate());

        patient.setParsedDate(new java.util.Date());
        patient.setUpdateDate(new java.util.Date());


        return patient;
    }

}
