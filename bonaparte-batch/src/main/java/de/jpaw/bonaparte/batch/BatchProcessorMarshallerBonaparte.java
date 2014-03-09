package de.jpaw.bonaparte.batch;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;

public class BatchProcessorMarshallerBonaparte implements BatchProcessorMarshaller<BonaPortable> {
    private static final String MEDIA_TYPE = "application/bonaparte";

	@Override
	public String getContentType() {
		return MEDIA_TYPE;
	}

	@Override
	public byte[] marshal(BonaPortable request) throws Exception {
		ByteArrayComposer bac = new ByteArrayComposer();
		bac.writeRecord(request);
		return bac.getBytes();
	}

	@Override
	public BonaPortable unmarshal(byte[] response, int length) throws Exception {
		ByteArrayParser bap = new ByteArrayParser(response, 0, length);
		return bap.readRecord();
	}
}
