package de.jpaw.bonaparte.batch;

public interface BatchProcessorMarshaller<X> {
	String getContentType();
	byte [] marshal(X request) throws Exception;
	X unmarshal(byte [] response, int length) throws Exception;
}
