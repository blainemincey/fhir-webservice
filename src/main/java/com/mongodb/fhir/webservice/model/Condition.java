package com.mongodb.fhir.webservice.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Condition Model Class using Spring Data
 * Part of the FHIR store and is stored as
 * part of the Patient model class.
 */
@Document
public class Condition {

    private String condition;
    private String conditionCode;
    private String verificationStatus;
    private String severity;
    private String clinicalStatus;
    private java.util.Date onsetDate;
    private java.util.Date dateParsed;

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

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getClinicalStatus() {
        return clinicalStatus;
    }

    public void setClinicalStatus(String clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    public Date getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(Date onsetDate) {
        this.onsetDate = onsetDate;
    }

    public Date getDateParsed() {
        return dateParsed;
    }

    public void setDateParsed(Date dateParsed) {
        this.dateParsed = dateParsed;
    }

    @Override
    public String toString() {
        return "ConditionModel{" +
                "condition='" + condition + '\'' +
                ", conditionCode='" + conditionCode + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", severity='" + severity + '\'' +
                ", clinicalStatus='" + clinicalStatus + '\'' +
                ", onsetDate=" + onsetDate +
                ", dateParsed=" + dateParsed +
                '}';
    }
}
