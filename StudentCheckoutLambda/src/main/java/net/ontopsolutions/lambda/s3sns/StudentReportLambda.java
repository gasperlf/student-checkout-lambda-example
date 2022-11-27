package net.ontopsolutions.lambda.s3sns;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;

import static net.ontopsolutions.lambda.utils.UtilityJackson.parser;

public class StudentReportLambda {

    public void handle(SNSEvent snsEvent) {
        snsEvent.getRecords().forEach(record -> {
          StudentSNSEvent studentSnsEvent = parser(record.getSNS().getMessage(), StudentSNSEvent.class);
            System.out.println(record.getSNS().getMessage());
        });
    }

}
