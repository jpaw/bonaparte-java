package de.jpaw.bonaparte.aws.test

import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.PublishRequest
import org.testng.annotations.Test

@Test
class AwsSNSTest {
    private static val MY_ENDPOINT = "https://sqs.eu-west-1.amazonaws.com"
    private static val MY_ARN = "arn:aws:sns:eu-west-1:777292991618:alerts"

    def private createClient() {
        return new AmazonSNSClient
    }

    def public void testPublishTopic() {
        val client = createClient
        client.endpoint = MY_ENDPOINT
        client.region = Region.getRegion(Regions.EU_WEST_1)

        //publish to an SNS topic
        val publishRequest = new PublishRequest(MY_ARN, "Hello, world of yesterday");
        val publishResult = client.publish(publishRequest);
        //print MessageId of message published to SNS topic
        println('''MessageId is «publishResult.messageId»''')
    }
}
