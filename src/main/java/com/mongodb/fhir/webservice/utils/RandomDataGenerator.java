package com.mongodb.fhir.webservice.utils;

import com.github.javafaker.Faker;
import com.mongodb.fhir.webservice.model.Condition;
import com.mongodb.fhir.webservice.model.Patient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Random data generator for app
 */
public class RandomDataGenerator {

    // Faker data
    private static Faker faker = new Faker();

    // Condition Types
    // These are actual HL7 condition codes and text
    private static List<ConditionType> conditionTypes
            = Arrays.asList(
            new ConditionType("33737001","Fracture of Rib"),
            new ConditionType("6142004","Influenza"),
            new ConditionType("7011001","Hallucinations"),
            new ConditionType("34361001","Traumatic cataract"),
            new ConditionType("34173009","Injury of radial artery")
    );

    private static ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();

    /**
     * Using Java faker lib to generate random patient data
     *
     * @return
     */
    public static Patient getPatient() {

        Patient patient = new Patient();
        patient.setPatientId(faker.idNumber().ssnValid());
        patient.setFirstName(faker.name().firstName());
        patient.setLastName(faker.name().lastName());
        patient.setAddress(faker.address().streetAddress());
        patient.setCity(faker.address().city());
        patient.setState(faker.address().state());
        patient.setPostalCode(faker.address().zipCode());
        patient.setGender(faker.demographic().sex());
        patient.setBirthDate(faker.date().birthday());

        // how many conditions should this patient have
        List<Condition> conditions = new ArrayList<Condition>();
        int numConditions = (int)(Math.random() * 5); // 5 max conditions
        for(int idx = 0; idx < numConditions; idx++) {
            conditions.add(RandomDataGenerator.getCondition());
        }
        patient.setConditions(conditions);

        return patient;
    }

    /**
     * Get a single random condition
     *
     * @return
     */
    public static Condition getCondition() {

        ConditionType conditionType = getRandomConditionType();

        Condition condition = new Condition();
        condition.setCondition(conditionType.getCondition());
        condition.setConditionCode(conditionType.getConditionCode());
        condition.setVerificationStatus("confirmed");
        condition.setSeverity("severe");
        condition.setClinicalStatus("active");
        // onset date within 30 days
        condition.setOnsetDate(faker.date().past(30, TimeUnit.DAYS));

        return condition;
    }

    /**
     * Grab a random condition type from static list
     *
     * @return
     */
    public static ConditionType getRandomConditionType() {
        int randomType = randomGenerator.nextInt(RandomDataGenerator.conditionTypes.size());

        return RandomDataGenerator.conditionTypes.get(randomType);
    }

    /**
     * Private inner class for the static list of condition types
     */
    private static class ConditionType {
        String conditionCode;
        String condition;

        /**
         *
         * @param conditionCode
         * @param condition
         */
        private ConditionType(String conditionCode, String condition) {
            this.condition = condition;
            this.conditionCode = conditionCode;
        }

        // getters
        public String getConditionCode() {
            return conditionCode;
        }
        public String getCondition() {
            return condition;
        }
    }

    /**
     * Smoke test the methods
     * @param args
     */
    public static void main(String[] args) {

    }

}
