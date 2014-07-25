package de.jpaw.bonaparte.batch;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

public class BatchProcessorFactoryRest<X> implements BatchProcessorFactory<X,X> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchProcessorFactoryRest.class);
    private final BatchProcessorMarshaller<X> marshaller;
    private int bufferSize = 1024 * 1024;
    private URL url = null;
    
    public BatchProcessorFactoryRest(BatchProcessorMarshaller<X> marshaller) {
        this.marshaller = marshaller;
    }
    
    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new FlaggedOption("url", JSAP.STRING_PARSER, null, JSAP.REQUIRED, 'U', "url", "remote URL"));
        params.registerParameter(new FlaggedOption("buffersize", JSAP.INTEGER_PARSER, "1000000", JSAP.NOT_REQUIRED, 'B', "rest-buffer-size", "buffer size for REST requests"));
    }

    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        bufferSize = params.getInt("buffersize");
        String remoteUrl = params.getString("url");
        try {
            url = new URL(remoteUrl);
        } catch (Exception e) {
            LOG.error("Cannot create URL from {}", remoteUrl);
        }
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public BatchProcessor<X,X> getProcessor(int threadNo) {
        return new BatchProcessorRest<X>(bufferSize, url, marshaller);
    }
    
    private static class BatchProcessorRest<X> implements BatchProcessor<X,X> {
        private final byte [] buffer;
        private final URL url;
        private final BatchProcessorMarshaller<X> marshaller;
        
        private BatchProcessorRest(int bufferSize, URL url, BatchProcessorMarshaller<X> marshaller) {
            buffer = new byte [bufferSize];
            this.url = url;
            this.marshaller = marshaller;
        }
        
        @Override
        public X process(int recordNo, X data) throws Exception {
            // get the raw data
            byte [] payload = marshaller.marshal(data);
            
            // 1.) create a connection to the target. This does not use any of the above SSL context.

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", marshaller.getContentType());
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + payload.length);
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(payload);
            // marshaller.marshal(data, connection.getOutputStream());

            wr.flush();

            // 4.) retrieve the response as required
            InputStream inputstream = connection.getInputStream();
            int length = 0;
            for (;;) {
                int morebytes = inputstream.read(buffer, length, buffer.length - length);
                if (morebytes > 0) {
                    length += morebytes;
                } else {
                    break;
                }
            }
            inputstream.close();
            wr.close();
            
            return marshaller.unmarshal(buffer, length);
        }

        @Override
        public void close() throws Exception {      // nothing to do, REST is connectionless
        }
    }

}
