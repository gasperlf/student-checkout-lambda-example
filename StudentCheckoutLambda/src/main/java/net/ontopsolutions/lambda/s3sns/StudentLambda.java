package net.ontopsolutions.lambda.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import net.ontopsolutions.lambda.utils.UtilityJackson;

import java.util.List;
import java.util.stream.Collectors;

public class StudentLambda {

    public static final String STUDENT_CHECKOUT_TOPIC = System.getenv("STUDENT_CHECKOUT_TOPIC");
    private final AmazonS3 amazonS3 = AmazonS3ClientBuilder.defaultClient();
    private final AmazonSNS amazonSNS = AmazonSNSClientBuilder.defaultClient();


    @SneakyThrows
    public void handle(S3Event event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Staring processing event from S3");
        event.getRecords().forEach(record -> {
            // read data from S3 bucket
            S3ObjectInputStream s3ObjectInputStream = getS3Object(record);
            List<StudentEvent> studentEventList = getListDto(s3ObjectInputStream);

            //s3ObjectInputStream.close();

            //calculate grade and mapping to new dto to publish in SNS topic
            List<StudentSNSEvent> studentSNSEvents = studentEventList.stream()
                    .map(this::mappingStudentS3Event)
                    .collect(Collectors.toList());

            //publish data into the SNS topic
            publishEvent(studentSNSEvents);
        });
    }

    private StudentSNSEvent mappingStudentS3Event(StudentEvent studentEvent) {
        StudentSNSEvent snsEvent = new StudentSNSEvent();
        snsEvent.setRollNo(studentEvent.getRollNo());
        snsEvent.setName(studentEvent.getName());
        snsEvent.setTestScore(studentEvent.getTestScore());
        snsEvent.setGrade(calculateGradeByScore(studentEvent.getTestScore()));
        return snsEvent;
    }

    private String calculateGradeByScore(int score) {
        if (score <= 30) {
            return "Grade A";
        }
        if (score > 30 && score <= 60) {
            return "Grade B";
        }

        if (score > 60 && score <= 80) {
            return "Grade C";
        }

        if (score > 80 && score < 95) {
            return "Grade D";
        }

        return "Grade C";
    }

    private void publishEvent(List<StudentSNSEvent> list) {
        list.forEach(student -> {
            amazonSNS.publish(STUDENT_CHECKOUT_TOPIC, UtilityJackson.parserString(student));
        });
    }

    private List<StudentEvent> getListDto(S3ObjectInputStream s3ObjectInputStream) {
        return UtilityJackson.parser(s3ObjectInputStream, new TypeReference<List<StudentEvent>>() {
        });
    }

    private S3ObjectInputStream getS3Object(final S3EventNotification.S3EventNotificationRecord record) {
        return amazonS3.getObject(record.getS3().getBucket().getName(),
                record.getS3().getObject().getKey()).getObjectContent();
    }


}
