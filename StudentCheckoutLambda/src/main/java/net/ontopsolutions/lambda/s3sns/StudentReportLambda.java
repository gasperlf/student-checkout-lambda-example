package net.ontopsolutions.lambda.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

import static net.ontopsolutions.lambda.utils.UtilityJackson.parser;

public class StudentReportLambda {

    public void handle(SNSEvent snsEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Staring processing event from S3");
        snsEvent.getRecords().forEach(record -> {
          StudentSNSEvent studentSnsEvent = parser(record.getSNS().getMessage(), StudentSNSEvent.class);
            System.out.println(record.getSNS().getMessage());
        });
    }

}
