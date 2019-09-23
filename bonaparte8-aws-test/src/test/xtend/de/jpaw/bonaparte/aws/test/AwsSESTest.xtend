package de.jpaw.bonaparte.aws.test

import org.testng.annotations.Test

import static extension org.testng.Assert.*
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.amazonaws.regions.Regions
import com.amazonaws.regions.Region

@Test
class AwsSESTest {

//    static final String FROM = "jpaw@jpaw.de";  // Replace with your "From" address. This address must be verified.
//    static final String TO = "jpaw@online.de"; // Replace with a "To" address. If your account is still in the
    static final String FROM = "Angela Merkel <Angela.Merkel@jpaw.de>";  // Replace with your "From" address. This address must be verified.
    static final String TO = "Michael.Bischoff@bertelsmann.de"; // Replace with a "To" address. If your account is still in the
                                                      // sandbox, this address must be verified.
    static final String BODY = "This isn't going to work";
    static final String SUBJECT = "Initial email";

    private static val MY_ENDPOINT = "https://sqs.eu-west-1.amazonaws.com"

    def private createClient() {
        return new AmazonSimpleEmailServiceClient
    }

    def public void testSendEmail() {

        // Construct an object to contain the recipient address.
        val destination = new Destination().withToAddresses(#[ TO ]);

        // Create the subject and body of the message.
        val subject = new Content().withData(SUBJECT);
        val textBody = new Content().withData(BODY);
        val body = new Body().withText(textBody);

        // Create a message with the specified subject and body.
        val message = new Message().withSubject(subject).withBody(body);

        // Assemble the email.
        val request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);

        // Instantiate an Amazon SES client, which will make the service call. The service call requires your AWS credentials.
        // Because we're not providing an argument when instantiating the client, the SDK will attempt to find your AWS credentials
        // using the default credential provider chain. The first place the chain looks for the credentials is in environment variables
        // AWS_ACCESS_KEY_ID and AWS_SECRET_KEY.
        // For more information, see http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
        val client = createClient

        // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your sandbox
        // status, sending limits, and Amazon SES identity-related settings are specific to a given AWS
        // region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
        // the US West (Oregon) region. Examples of other regions that Amazon SES supports are US_EAST_1
        // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
        client.endpoint = MY_ENDPOINT
        client.region = Region.getRegion(Regions.EU_WEST_1)

        // Send the email.
        val result = client.sendEmail(request);
        println('''result is «result.toString»''')
        println("Email sent!");
    }
}
