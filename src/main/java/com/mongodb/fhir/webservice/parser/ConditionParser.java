package com.mongodb.fhir.webservice.parser;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.mongodb.fhir.webservice.model.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to parse FHIR Condition Resource messages.
 * Uses Hapi-Fhir libs
 */
public class ConditionParser {

    // Logger component
    private static final Logger log = LoggerFactory.getLogger(ConditionParser.class);

    /**
     *
     * @param conditionResource
     */
    public static Object[] parse(String conditionResource) {

        Object conditionResults[] = null;

        if(conditionResource != null && conditionResource.length() > 1) {
            conditionResults = parseCondition(conditionResource);
        }

        return conditionResults;
    }

    /**
     *
     * @param conditionResource
     * @return
     */
    private static Object[] parseCondition(String conditionResource) {

        // return an Object array.
        // First element is condition entity, second is the subject reference to associate to patient
        Object[] conditionResults = new Object[2];

        Condition condition = new Condition();

        // Create a FHIR context
        FhirContext ctx = FhirContext.forR5();

        // Instantiate a new parser
        IParser parser = ctx.newJsonParser();

        // Parse it
        org.hl7.fhir.r5.model.Condition parsed = parser.parseResource(org.hl7.fhir.r5.model.Condition.class, conditionResource);

        // set parsed values
        condition.setCondition(parsed.getCode().getCodingFirstRep().getDisplay());
        condition.setConditionCode(parsed.getCode().getCodingFirstRep().getCode());
        condition.setClinicalStatus(parsed.getClinicalStatus().getCodingFirstRep().getCode());
        condition.setVerificationStatus(parsed.getVerificationStatus().getCodingFirstRep().getCode());
        condition.setSeverity(parsed.getSeverity().getCodingFirstRep().getDisplay());
        condition.setOnsetDate(parsed.getOnset().dateTimeValue().getValue());
        condition.setDateParsed(new java.util.Date());

        // set array elements
        conditionResults[0] = condition;
        conditionResults[1] = parsed.getSubject().getReference();

        return conditionResults;
    }

}
