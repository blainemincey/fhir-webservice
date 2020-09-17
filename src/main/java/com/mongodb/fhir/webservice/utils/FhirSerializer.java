package com.mongodb.fhir.webservice.utils;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r5.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Serialize Java objects to FHIR Resource string
 */
public class FhirSerializer {

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(FhirSerializer.class);

    // Create a FHIR context
    private static FhirContext ctx = FhirContext.forR5();

    /**
     *
     */
    public FhirSerializer() {

    }

    /**
     * Serialize patient model to fhir resource
     *
     * @param myPatient
     * @return
     */
    public static String serializePatient(com.mongodb.fhir.webservice.model.Patient myPatient) {

        // Create a Patient resource to serialize
        Patient patient = new Patient();
        patient.setId(myPatient.getPatientId());
        patient.addName().setFamily(myPatient.getLastName()).addGiven(myPatient.getFirstName());
        if(myPatient.getGender().equalsIgnoreCase(Enumerations.AdministrativeGender.MALE.toString())) {
            patient.setGender(Enumerations.AdministrativeGender.MALE);
        } else {
            patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        }
        patient.setBirthDate(myPatient.getBirthDate());

        // address
        Address address = new Address();
        List lineList = new ArrayList();
        StringType addressLine = new StringType(myPatient.getAddress());
        lineList.add(addressLine);

        address.setLine(lineList);
        address.setCity(myPatient.getCity());
        address.setState(myPatient.getState());
        address.setPostalCode(myPatient.getPostalCode());

        List addressList = new ArrayList();
        addressList.add(address);
        patient.setAddress(addressList);

        // Instantiate a new JSON parser
        IParser parser = ctx.newJsonParser();

        // Serialize it
        String serialized = parser.encodeResourceToString(patient);

        return serialized;
    }

    /**
     * Serialize condition model to FHIR resource
     *
     * @param myCondition
     * @return
     */
    public static String serializeCondition(com.mongodb.fhir.webservice.model.Condition myCondition, String subjectId) {
        Condition condition = new Condition();

        // Clinical Status
        Coding clinicalStatusCode = new Coding().setCode(myCondition.getClinicalStatus());
        ArrayList clinicalStatusCodeList = new ArrayList();
        clinicalStatusCodeList.add(clinicalStatusCode);
        condition.setClinicalStatus(new CodeableConcept().setCoding(clinicalStatusCodeList));

        // Verification Status
        Coding verificationStatusCode = new Coding().setCode(myCondition.getVerificationStatus());
        ArrayList verificationStatusCodeList = new ArrayList();
        verificationStatusCodeList.add(verificationStatusCode);
        condition.setVerificationStatus(new CodeableConcept().setCoding(verificationStatusCodeList));

        // Severity
        Coding severityCode = new Coding().setDisplay(myCondition.getSeverity());
        ArrayList severityCodeList = new ArrayList();
        severityCodeList.add(severityCode);
        condition.setSeverity(new CodeableConcept().setCoding(severityCodeList));

        // Condition and condition code
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setCode(myCondition.getConditionCode());
        coding.setDisplay(myCondition.getCondition());
        List<Coding> codingList = new ArrayList<Coding>();
        codingList.add(coding);
        codeableConcept.setCoding(codingList);
        condition.setCode(codeableConcept);

        // onset date
        DateTimeType dateTimeType = new DateTimeType(myCondition.getOnsetDate());
        condition.setOnset(dateTimeType);

        // subject reference
        Reference reference = new Reference();
        reference.setReference(subjectId);
        condition.setSubject(reference);

        // Instantiate a new JSON parser
        IParser parser = ctx.newJsonParser();

        // Serialize it
        String serialized = parser.encodeResourceToString(condition);

        return serialized;
    }

    /**
     * Method to test class
     *
     * @param args
     */
    public static void main(String[] args) {
        //FhirSerializer.serializePatient(RandomDataGenerator.getPatient());
        FhirSerializer.serializeCondition(RandomDataGenerator.getCondition(), "11-22");
    }
}
