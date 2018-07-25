package de.jpaw.bonaparte.sock;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.HttpPostResponseObject;

public interface INetworkDialog2 {
    /** Retrieves a response for a request (synchronous blocking operation for testing / demonstration purposes, in production you should use asynchronous nonblocking I/O).
     * Returns a response object with the relevant information about the success (or throws an Exception, in case of response parsing errors). */
	HttpPostResponseObject doIO2(BonaPortable request) throws Exception;
}
