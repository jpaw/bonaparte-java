package de.jpaw.bonaparte.sock;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.util.IMarshaller;
import de.jpaw.bonaparte.util.impl.RecordMarshallerBonaparte;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

/**
 * Client connection via http. This class provides a request / response functionality implemented via http POST.
 * The http authorization header can be modifed during the lifetime of an instance.
 * The marshaller can be changed as well, this is however usually not desired.
 * 
 * @author mbi
 *
 */
public class HttpPostClient implements INetworkDialog {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpPostClient.class);
    
    protected final URL url;
    private final boolean logSizes;
    private final boolean logText;
    private final boolean logHex;
    protected IMarshaller marshaller;
    protected String authentication = null;
    
    public HttpPostClient(URL url, boolean logSizes, boolean logText, boolean logHex, IMarshaller initialMarshaller) {
        this.url        = url;
        this.logSizes   = logSizes;
        this.logText    = logText;
        this.logHex     = logHex;
        this.marshaller = initialMarshaller;
    }
    
    public HttpPostClient(URL url) {
        this(url, false, false, false, new RecordMarshallerBonaparte());
    }
    
    public void setMarshaller(IMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    /** Execute the request / response dialog. */ 
    @Override
    public BonaPortable doIO(BonaPortable request) throws Exception {
        ByteArray serializedRequest = marshaller.marshal(request);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", marshaller.getContentType());
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", "" + serializedRequest.length());
        connection.setUseCaches(false);
        
        if (authentication != null)
            connection.setRequestProperty("Authorization", authentication);

        if (logSizes)
            LOGGER.info("{} serialized as {} bytes for MIME type {}", request.ret$PQON(), serializedRequest.length(), marshaller.getContentType());
        if (logText)
            LOGGER.info("Request is <{}>", serializedRequest.toString());
        if (logHex)
            LOGGER.info(serializedRequest.hexdump(0, 0));

        // write the request
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        serializedRequest.toOutputStream(wr);
        wr.flush();
    
        // retrieve the response
        InputStream is = connection.getInputStream();
        
        // after getInputStream, the status should be available: https://www.tbray.org/ongoing/When/201x/2012/01/17/HttpURLConnection
        int returnCode = connection.getResponseCode();
        String statusMessage = connection.getResponseMessage();
        
        if (returnCode != HttpURLConnection.HTTP_OK) {
            LOGGER.warn("response for {} is HTTP {} ({})", request.ret$PQON(), returnCode, statusMessage);
            return null;
        }
        
        ByteBuilder serializedResponse = new ByteBuilder();
        serializedResponse.readFromInputStream(is, 0);
        is.close();
        wr.close();
    
        if (logSizes)
            LOGGER.info("retrieved {} bytes response", serializedResponse.length());
        if (logText)
            LOGGER.info("Response is <{}>", serializedResponse.toString());
        if (logHex)
            LOGGER.info(serializedResponse.hexdump(0, 0));

        return marshaller.unmarshal(serializedResponse);
    }
}
