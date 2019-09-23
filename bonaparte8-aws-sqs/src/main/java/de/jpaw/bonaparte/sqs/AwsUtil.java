package de.jpaw.bonaparte.sqs;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

public class AwsUtil {
    public static AmazonS3Client getDefaultClient() {
        return new AmazonS3Client(new ProfileCredentialsProvider().getCredentials());
    }
}
