package de.jpaw.bonaparte.batch;

import java.io.OutputStream;

public interface BatchProcessorMarshaller<X> {
	String getContentType();
	
	byte [] marshal(X request) throws Exception;  					// may be slow due to coyping
	void marshal(X request, OutputStream w) throws Exception;		// preferred method (simple implementation is w.write(marshal(request)))
	
	X unmarshal(byte [] response, int length) throws Exception;		// may be slow due to coyping
}
