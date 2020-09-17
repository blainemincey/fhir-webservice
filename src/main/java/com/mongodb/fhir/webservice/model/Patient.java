package com.mongodb.fhir.webservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Patient model class using Spring Data.
 * This is part of the FHIR store.
 *
 * Several fields in this specific class are encrypted when
 * using the encrypted Mongo client.
 */
@Document
public class Patient {

    // ObjectId
    @Id
    private String id;

    // Patient Id
    private String patientId;

    // Name
    private String firstName;
    private String lastName;

    // Address
    private String address;
    private String city;
    private String state;
    private String postalCode;

    // Gender
    private String gender;

    // Birth date
    private java.util.Date birthDate;

    // List of conditions
    List<Condition> conditions;

    // Date parsed/updated
    private java.util.Date parsedDate;
    private java.util.Date updateDate;

    /**
     *
     */
    public Patient() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Date getParsedDate() {
        return parsedDate;
    }

    public void setParsedDate(Date parsedDate) {
        this.parsedDate = parsedDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", conditions=" + conditions +
                ", parsedDate=" + parsedDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
