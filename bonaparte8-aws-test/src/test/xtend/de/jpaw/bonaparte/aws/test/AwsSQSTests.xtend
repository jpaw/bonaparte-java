package de.jpaw.bonaparte.aws.test

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.sqs.AmazonSQSClient
import de.jpaw.bonaparte.pojos.s3Tests.Test1
import org.testng.annotations.Test

import static extension org.testng.Assert.*
import de.jpaw.bonaparte.sqs.BonaparteAwsSqsSink
import com.amazonaws.services.sqs.model.ReceiveMessageRequest

@Test
class AwsSqsTest {
    private static val MY_QUEUE = "jpawTest"
    private static val MY_ENDPOINT = "https://sqs.eu-central-1.amazonaws.com"

    def private createClient() {
        val credentials = (new ProfileCredentialsProvider).credentials
        return new AmazonSQSClient(credentials)
    }

    def private void sendNObjects(int numRecords) {
        val composer = new BonaparteAwsSqsSink(MY_QUEUE, MY_ENDPOINT)
        val now = System.currentTimeMillis
        for (var int i = 1; i <= numRecords; i += 1) {
            val obj = new Test1(now, i, '''Message no «i»''')
            composer.writeRecord(obj)
        }
        val then = System.currentTimeMillis
        println('''Time taken for «numRecords» records: «then - now» millseconds («(then - now) as double / (numRecords as double)» ms / record''')
    }

    def public void testSend5() {
        sendNObjects(5)
    }

    def public void testSend20() {
        sendNObjects(20)
    }

//    def public void testSend100() {
//        sendNObjects(100)
//    }


    def public void testReceiveAllFromQueue() {
        val delete = true
        val client = createClient
        client.endpoint = MY_ENDPOINT
        val queueUrl = client.getQueueUrl(MY_QUEUE).queueUrl
        for (;;) {
            val receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
            val messages = client.receiveMessage(receiveMessageRequest).messages
            if (messages.isNullOrEmpty)
                return;
            println('''***** Got «messages.size» messages *****''')
            messages.forEach [
                println('''message ID is «messageId», body is «body»''')
                println('''Attributes are: «attributes.entrySet.map['''key=«key», value=«value»'''].join("; ")»''')
                if (delete)
                    client.deleteMessage(queueUrl, receiptHandle)
            ]
        }
    }

    // default client: got queue URL https://sqs.us-east-1.amazonaws.com/777292991618/mydummy
    def public void testListQueues() {
        createClient.listQueues.queueUrls.forEach [
            println('''got queue URL «it»''')
        ]
    }

    // default client: got queue URL https://sqs.us-east-1.amazonaws.com/777292991618/mydummy
    def public void testListQueuesForFrankfurt() {
        val client = createClient
        client.endpoint = "https://sqs.eu-central-1.amazonaws.com"
        // println('''Region is «client.region»''')
        client.listQueues.queueUrls.forEach [
            println('''got queue URL «it»''')
        ]
    }

    // default client: got queue URL https://sqs.us-east-1.amazonaws.com/777292991618/mydummy
    def public void testListQueuesForIreland() {
        val client = createClient
        client.endpoint = "https://sqs.eu-west-1.amazonaws.com"
        // println('''Region is «client.region»''')
        client.listQueues.queueUrls.forEach [
            println('''got queue URL «it»''')
        ]
    }

    def public void testCreateQueue() {
        val result = createClient.createQueue("mydummy")
        result.assertNotNull
        println('''Queue URL is «result.queueUrl»''')
    }
}
