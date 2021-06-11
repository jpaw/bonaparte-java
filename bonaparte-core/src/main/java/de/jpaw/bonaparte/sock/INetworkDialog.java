package de.jpaw.bonaparte.sock;

import de.jpaw.bonaparte.core.BonaPortable;

@Deprecated
public interface INetworkDialog {
    /** Retrieves a response for a request (synchronous blocking operation for testing / demonstration purposes, in production you should use asynchronous nonblocking I/O).
     * Returns null in case of a http response code != 2xx or other network error (or throws an Exception). */
    BonaPortable doIO(BonaPortable request) throws Exception;
}
