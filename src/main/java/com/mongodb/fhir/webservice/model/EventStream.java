package com.mongodb.fhir.webservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Separate example of an Event Store persisting each
 * event within the FHIR server.
 */
@Document
public class EventStream {

    // Resource type constants
    public static final String PATIENT_RESOURCE = "Patient";
    public static final String CONDITION_RESOURCE = "Condition";
    public static final String PATIENT_ID_RESOURCE = "Patient_Id";

    // Event method constants
    public static final String HTTP_GET = "HTTP_GET";
    public static final String HTTP_POST = "HTTP_POST";

    @Id
    private String id;
    private Date eventDate;
    private String rawEventString;
    private String eventMethod;
    private String resourceType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getRawEventString() {
        return rawEventString;
    }

    public void setRawEventString(String rawEventString) {
        this.rawEventString = rawEventString;
    }

    public String getEventMethod() {
        return eventMethod;
    }

    public void setEventMethod(String eventMethod) {
        this.eventMethod = eventMethod;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String toString() {
        return "EventStream{" +
                "id='" + id + '\'' +
                ", eventDate=" + eventDate +
                ", rawEventString='" + rawEventString + '\'' +
                ", eventMethod='" + eventMethod + '\'' +
                ", resourceType='" + resourceType + '\'' +
                '}';
    }
}
