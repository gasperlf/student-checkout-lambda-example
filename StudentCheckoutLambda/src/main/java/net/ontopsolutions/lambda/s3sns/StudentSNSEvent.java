package net.ontopsolutions.lambda.s3sns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class StudentSNSEvent {

    private String rollNo;
    private String name;
    private int testScore;
    private String grade;
}
