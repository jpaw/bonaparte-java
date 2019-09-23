package de.jpaw.bonaparte8.batch.marshaller;

import java.io.OutputStream;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw8.batch.api.BatchMarshaller;

public class BatchMarshallerBonaparte implements BatchMarshaller<BonaPortable> {

    @Override
    public String getContentType() {
        return MimeTypes.MIME_TYPE_BONAPARTE;
    }

    @Override
    public byte[] marshal(BonaPortable request) throws Exception {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(request);
        return bac.getBytes();
    }
    @Override
    public void marshal(BonaPortable request, OutputStream w) throws Exception {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(request);
        w.write(bac.getBuffer(), 0, bac.getLength());       // this one avoids a byte [] copy
    }


    @Override
    public BonaPortable unmarshal(byte[] response, int length) throws Exception {
        ByteArrayParser bap = new ByteArrayParser(response, 0, length);
        return bap.readRecord();
    }
}
