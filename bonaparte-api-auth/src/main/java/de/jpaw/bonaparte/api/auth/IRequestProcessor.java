package de.jpaw.bonaparte.api.auth;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.api.auth.JwtInfo;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

/** Interface between a specific server implementation (for example servlet, netty, or vert.x based) and the execution backend.
 * Can also be used for a client, in which case the implementation will send the request to the remote server. */ 
public interface IRequestProcessor<RQ extends BonaPortable, RS extends BonaPortable> {
    ObjectReference getRequestRef();
    ObjectReference getResponseRef();
    
    /** execute a request. For server implementations, authentication has been performed,jwtInfo is the decoded user / session information,
     * and encodedJwt provides the signed token in case nested executions have to be performed.  
     * Implementations have to catch all exceptions and populate a return code and error details in case anything fails.
     */
    RS execute(RQ rq, JwtInfo jwtInfo, String encodedJwt);
    
    int getReturnCode(RS rs);
    String getErrorDetails(RS rs);
}
