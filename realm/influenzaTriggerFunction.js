//
// Trigger Function to notify when an Influenza condition is reported
//
exports = async function(changeEvent) {
    console.log("Executing InfluenzaTriggerFunction.");

    if(changeEvent.operationType === 'insert') {
        console.log("Insert operation on results collection." );

        // Get the full document
        const fullDocument = changeEvent.fullDocument;
        const condition = changeEvent.fullDocument.condition;

        if(condition === 'Influenza') {
            console.log("Influenza Event.  Send notification!");

            // get a handle to the influenza events collection
            var influenzaEventsCollection = context.services.get("mongodb-atlas").db("fhirReporting").collection("influenzaEvents");

            await influenzaEventsCollection.count()
                .then(numDocs => {
                    console.log(`${numDocs} influenza events.`);

                    var message = `${fullDocument.gender} in ${fullDocument.city}, ${fullDocument.state} has tested positive for Influenza`;

                    const twilio = context.services.get("myTwilioService");
                    twilio.send({
                        "to": "+11111111111",
                        "from": "+2222222222",
                        "body": message });
                })
                .catch(err => {
                    console.error("Failed to count documents: ", err)
                })
        }
    }
};
