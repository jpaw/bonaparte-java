package de.jpaw.bonaparte.sqs;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.ObjectReuseStrategy;
import de.jpaw.util.ByteBuilder;

public class BonaparteAwsSqsSink {
    protected final ByteBuilder buffer;
    protected final CompactByteArrayComposer cbac;
    protected final String queueName;
    protected final String queueUrl;
    protected final AmazonSQSClient s3client;

    public BonaparteAwsSqsSink(String queueName, int initialBufferSize, ObjectReuseStrategy strategy, AWSCredentials credentials, ClientConfiguration clientConfiguration, String endpoint) {
        buffer = new ByteBuilder(initialBufferSize > 0 ? initialBufferSize : 1000, StandardCharsets.UTF_8);
        cbac = new CompactByteArrayComposer(buffer, strategy, false);
        this.queueName = queueName;
        s3client = new AmazonSQSClient(credentials, clientConfiguration);
        if (endpoint != null)
            s3client.setEndpoint(endpoint);
        queueUrl = s3client.getQueueUrl(queueName).getQueueUrl();
    }

    public BonaparteAwsSqsSink(String queueName, String endpoint) {
        this(queueName, 0, ObjectReuseStrategy.defaultStrategy, new ProfileCredentialsProvider().getCredentials(), new ClientConfiguration(), endpoint);
    }

    public void writeRecord(BonaCustom obj) {
        cbac.reset();
        cbac.writeRecord(obj);
        // push the data to the queue. It must be base64 encoded, because SQS cannot deal with characters like ctrl-A
        String data = Base64.getEncoder().encodeToString(buffer.getBytes());  // Encoder is really missing a method to encode a buffer delimited by length. This would avoid an object construction / array copy.
        s3client.sendMessage(queueName, data);
        cbac.reset();
    }
}
