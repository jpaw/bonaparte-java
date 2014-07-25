package de.jpaw.bonaparte.batch;

import java.io.OutputStream;
import java.util.Arrays;

public class BatchProcessorMarshallerRaw implements BatchProcessorMarshaller<byte []> {
    private static final String MEDIA_TYPE = "application/octet-stream";

    @Override
    public String getContentType() {
        return MEDIA_TYPE;
    }

    @Override
    public byte[] marshal(byte [] request) throws Exception {
        return request;
    }

    @Override
    public void marshal(byte[] request, OutputStream w) throws Exception {
        w.write(request);
    }
    
    @Override
    public byte [] unmarshal(byte[] response, int length) throws Exception {
        if (response == null || response.length == length)
            return response;
        return Arrays.copyOf(response, length);
    }
}
