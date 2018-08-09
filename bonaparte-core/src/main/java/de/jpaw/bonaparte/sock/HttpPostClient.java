package de.jpaw.bonaparte.sock;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.HttpPostResponseObject;
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
public class HttpPostClient implements INetworkDialog, INetworkDialog2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpPostClient.class);

    protected String baseUrl;
    private final boolean addVariableUrlPath;
    private final boolean logSizes;
    private final boolean logText;
    private final boolean logHex;
    private URL cachedUrl = null;
    protected IMarshaller marshaller;
    protected String authentication = null;
    protected int timeoutInMs = 5000;

    public HttpPostClient(String baseUrl, boolean addVariableUrlPath, boolean logSizes, boolean logText, boolean logHex, IMarshaller initialMarshaller) {
        this.baseUrl            = baseUrl;
        this.addVariableUrlPath = addVariableUrlPath;
        this.logSizes           = logSizes;
        this.logText            = logText;
        this.logHex             = logHex;
        this.marshaller         = initialMarshaller;
    }

    public HttpPostClient(String baseUrl) {
        this(baseUrl, false, false, false, false, new RecordMarshallerBonaparte());
    }

    public void setMarshaller(IMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setTimeoutInMs(int newTimeout) {
        this.timeoutInMs = newTimeout;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        cachedUrl = null;       // reset cached converted URL if changing the base path!
    }

    // properties can be set in their own method to allow overriding it
    protected void setRequestProperties(HttpURLConnection connection) {
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(timeoutInMs);
        connection.setRequestProperty("Content-Type",   marshaller.getContentType());
        connection.setRequestProperty("Accept",         marshaller.getContentType());
        connection.setRequestProperty("Charset",        "utf-8");
        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.setUseCaches(false);
    }

    protected void requestLogger(String pqon, ByteArray serializedRequest) {
        if (logSizes)
            LOGGER.info("{} serialized as {} bytes for MIME type {}", pqon, serializedRequest.length(), marshaller.getContentType());
        if (logText)
            LOGGER.info("Request is <{}>", serializedRequest.toString());
        if (logHex)
            LOGGER.info(serializedRequest.hexdump(0, 0));
    }

    protected void responseLogger(ByteBuilder serializedResponse) {
        if (logSizes)
            LOGGER.info("retrieved {} bytes response", serializedResponse.length());
        if (logText)
            LOGGER.info("Response is <{}>", serializedResponse.toString());
        if (logHex)
            LOGGER.info(serializedResponse.hexdump(0, 0));
    }

    protected BonaPortable errorReturn(String requestPqon, int returnCode, String statusMessage) throws Exception {
        LOGGER.warn("response for {} is HTTP {} ({})", requestPqon, returnCode, statusMessage);
        return null;
    }

    /** Execute the request / response dialog. */
    @Override
    public BonaPortable doIO(BonaPortable request) throws Exception {
        return doIO2(request).getResponseObject();
    }

    /** Execute the request / response dialog. */
    @Override
    public HttpPostResponseObject doIO2(BonaPortable request) throws Exception {
        ByteArray serializedRequest = ByteArray.ZERO_BYTE_ARRAY;
        String requestPqon          = "LOGOUT";

        if (request != null) {
            serializedRequest   = marshaller.marshal(request);
            requestPqon         = request.ret$PQON();
        }

        URL url = null;
        if (addVariableUrlPath && request != null) {
            String variablePath = request.ret$BonaPortableClass().getProperty("path");
            if (variablePath != null) {
                url = new URL(baseUrl + "/" + variablePath);
            }
        }
        if (url == null) {
            if (cachedUrl == null)
                cachedUrl = new URL(baseUrl);
            url = cachedUrl;
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        setRequestProperties(connection);

        if (authentication != null)
            connection.setRequestProperty("Authorization", authentication);

        connection.setRequestProperty("Content-Length", "" + serializedRequest.length());
        requestLogger(requestPqon, serializedRequest);

        // write the request
        OutputStream wr = connection.getOutputStream();
        serializedRequest.toOutputStream(wr);
        wr.flush();

        // according to https://www.tbray.org/ongoing/When/201x/2012/01/17/HttpURLConnection the status should be available
        // after getInputStream for GET
        // before for POST
        // in either case, swapping the order would cause a nasty IOException!

        int returnCode = connection.getResponseCode();
        String statusMessage = connection.getResponseMessage();

        if ((returnCode / 100) != (HttpURLConnection.HTTP_OK / 100)) {   // accept 200, 201, etc...
            wr.close();
            BonaPortable errorResp = errorReturn(requestPqon, returnCode, statusMessage);
            return new HttpPostResponseObject(returnCode, statusMessage, errorResp);
        }

        // retrieve the response
        InputStream is = connection.getInputStream();

        ByteBuilder serializedResponse = new ByteBuilder();
        serializedResponse.readFromInputStream(is, 0);
        is.close();
        wr.close();

        responseLogger(serializedResponse);

        return new HttpPostResponseObject(returnCode, statusMessage, serializedResponse.length() == 0 ? null : marshaller.unmarshal(serializedResponse));
    }
}
