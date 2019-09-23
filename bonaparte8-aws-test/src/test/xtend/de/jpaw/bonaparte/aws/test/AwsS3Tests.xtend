package de.jpaw.bonaparte.aws.test

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import de.jpaw.bonaparte.core.CompactByteArrayComposer
import de.jpaw.bonaparte.pojos.s3Tests.Test1
import java.io.ByteArrayInputStream
import org.testng.annotations.Test

import static extension org.testng.Assert.*
import com.amazonaws.services.s3.model.ListObjectsRequest
import java.io.File
import com.amazonaws.services.s3.model.PutObjectRequest
import java.util.Date
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.HttpMethod
import de.jpaw.bonaparte.core.MimeTypes

@Test
class AwsS3Test {
    private static val MY_BUCKET = "my1st-bucket"

    def private createClient() {
        val credentials = (new ProfileCredentialsProvider).credentials
        return new AmazonS3Client(credentials)
    }

    def public void testCreateBucket() {
        val s3client = createClient
        val bucket = s3client.createBucket(MY_BUCKET)
        bucket.assertNotNull
    }


    def public void testListBuckets() {
        createClient.listBuckets.forEach [
            println('''got bucket «name»''')
        ]
    }

    def public void testListFolder() {
        val s3client = createClient
        val listObjectsRequest = new ListObjectsRequest().withBucketName(MY_BUCKET).withPrefix("AwsS3Test/").withDelimiter("#");
        val objects = s3client.listObjects(listObjectsRequest)
        objects.objectSummaries.forEach[
            println('''S3 object «key» (by «owner») has size «size» and class «storageClass»''')
        ]
    }

    def public void testS3upload() {
        val id = Long.toString(System.currentTimeMillis, 16)
        val s3client = createClient

        val data = new Test1(System.currentTimeMillis, 12, "hello, world")
        val binaryData = CompactByteArrayComposer.marshal(Test1.meta$$this, data)
        val stream = new ByteArrayInputStream(binaryData)
        val meta = new ObjectMetadata => [
            contentLength = binaryData.length
            contentType   = MimeTypes.MIME_TYPE_BONAPARTE
        ]
        s3client.putObject(MY_BUCKET, "AwsS3Test/S3upload-" + id, stream, meta);
    }

    def public void testS3Bucket2() {
        val s3client = createClient
        s3client.createBucket("aroma-beo")
    }

    static final String BEO_BUCKET = "aroma-beo";
    static final String BEO_KEY = "iOS/beo.png";


    def public void testS3FileUpload() {
        val s3client = createClient
        // s3client.createBucket("beo42")
        val meta = new ObjectMetadata => [
            contentType   = "image/png"
        ]
        s3client.putObject(new PutObjectRequest(BEO_BUCKET, BEO_KEY, new File("/tmp/beo.png")).withMetadata(meta));
    }


    def public void testS3CreatePreSignedUrl() {
        val s3client = createClient
        val myExpiration = new Date => [
            time = time + 1000 * 60 * 60 * 24 * 7; // 1 week
        ]
        val generatePresignedUrlRequest = new GeneratePresignedUrlRequest(BEO_BUCKET, BEO_KEY) => [
            method = HttpMethod.GET
            expiration = myExpiration
        ]
        val url = s3client.generatePresignedUrl(generatePresignedUrlRequest)
        println('''Generated URL is «url»''')
    }
}
