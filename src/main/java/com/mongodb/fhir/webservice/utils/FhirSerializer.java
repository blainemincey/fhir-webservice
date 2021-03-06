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
     * Serialize patient model to fhir patient resource
     *
     * @param myPatient
     * @return
     */
    public static Patient serializeFhirPatient(com.mongodb.fhir.webservice.model.Patient myPatient) {

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

        return patient;
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
     * Serialize condition model to FHIR condition
     *
     * @param myCondition
     * @return
     */
    public static Condition serializeFhirCondition(com.mongodb.fhir.webservice.model.Condition myCondition, String subjectId) {
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

        return condition;
    }

    /**
     *
     * @param myPatient
     * @return
     */
    public static String serializeBundle(com.mongodb.fhir.webservice.model.Patient myPatient) {
        log.info("Serializing Bundle.");

        String bundleAsString = "";

        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);

        // Serialize the patient as Bundle Resource
        Patient patient = FhirSerializer.serializeFhirPatient(myPatient);
        if(patient != null) {

            bundle
                    .addEntry()
                    .setFullUrl(patient.getIdElement().getValue())
                    .setResource(patient)
                    .getRequest()
                    .setUrl("Patient")
                    .setMethod(Bundle.HTTPVerb.POST);

        } else {
            log.info("Patient not serialized.  Null value.");
        }

        // Loop and serialize each condition as part of Bundle Resource
        List<com.mongodb.fhir.webservice.model.Condition> conditionArrayList = myPatient.getConditions();
        if(conditionArrayList != null && conditionArrayList.size() > 0) {

            // loop and serialize to bundle
            for(com.mongodb.fhir.webservice.model.Condition myCondition : conditionArrayList) {

                Condition condition = FhirSerializer.serializeFhirCondition(myCondition, myPatient.getPatientId());

                bundle.addEntry()
                        .setResource(condition)
                        .getRequest()
                        .setUrl("Condition")
                        .setMethod(Bundle.HTTPVerb.POST);
            }

        } else {
            log.info("No conditions to serialize as part of Bundle for patientId: " + myPatient.getPatientId());
        }

        // Convert bundle to string in order to return to client
        FhirContext ctx = FhirContext.forR5();
        bundleAsString = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

        return bundleAsString;
    }

    /**
     * Method to test class
     *
     * @param args
     */
    public static void main(String[] args) {
        //FhirSerializer.serializePatient(RandomDataGenerator.getPatient());
        //FhirSerializer.serializeCondition(RandomDataGenerator.getCondition(), "11-22");
        System.out.println(FhirSerializer.serializeBundle(RandomDataGenerator.getPatient()));
    }
}
