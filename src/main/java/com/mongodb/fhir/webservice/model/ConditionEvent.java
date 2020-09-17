package com.mongodb.fhir.webservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Event Streaming model class for Conditions.
 * This is essentially the Event Store.
 */
@Document
public class ConditionEvent {

    @Id
    private String id;
    private String city;
    private String state;
    private String gender;
    private Date birthdate;
    private String condition;
    private String conditionCode;
    private java.util.Date onsetDate;
    private java.util.Date reportedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public Date getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(Date onsetDate) {
        this.onsetDate = onsetDate;
    }

    public Date getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Date reportedDate) {
        this.reportedDate = reportedDate;
    }

    @Override
    public String toString() {
        return "ConditionEvent{" +
                "id='" + id + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", gender='" + gender + '\'' +
                ", birthdate=" + birthdate +
                ", condition='" + condition + '\'' +
                ", conditionCode='" + conditionCode + '\'' +
                ", onsetDate=" + onsetDate +
                ", reportedDate=" + reportedDate +
                '}';
    }
}
