/*
 * Copyright 2012 Michael Bischoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jpaw.util;

import java.util.HashMap;
import java.util.Map;

/**
 *          Defines the parent class for all application errors which are
 *          thrown in the marshalling / unmarshalling or messaging areas, as well as
 *          the application modules themselves.
 *          <p>
 *          Error codes are defined in a way such that the 8th digit (error code divided by 10 to the power of 8)
 *          provides a good classification of the problem.
 *          The classifications provided are actually targeting at full application coverage and not only message serialization / deserialization.
 *
 * @author Michael Bischoff
 *
 */

public class ApplicationException extends Exception {
    private static final long serialVersionUID = 1122421467960337766L;


    /** The classification of return codes indicating success (which would never be instantiated as an exception). */
    static public final int SUCCESS = 0;
    /** The classification of return codes indicating a decline or negative decision, without being a parameter or processing problem, and therefore also never be instantiated as an exception. */
    static public final int DENIED  = 1;
    /** The classification of return codes indicating an invalid message format. */
    static public final int PARSER_ERROR = 2;
    /** The classification of return codes indicating an invalid reference or field value (for example an invalid customer no or invalid country code). */
    static public final int PARAMETER_ERROR = 3;
    /** The classification of return codes indicating a processing timeout. The requester should react by resending the request some time later. The resource was available but did not respond back in the expected time. */
    static public final int TIMEOUT = 4;
    /** The classification of return codes indicating a (hopefully temporary) problem of resource shortage (no free sockets, disk full, cannot fork due to too many processes...). */
    static public final int RESOURCE_EXHAUSTED = 5;
    /** The classification of return codes indicating a resource or service which is temporarily unavailable. This could be due to a downtime of an OSGi component or remote service. Senders should treat such return code similar to a timeout return code and retry later. */
    static public final int SERVICE_UNAVAILABLE = 6;
    /** An intermediate classification returned by internal validation algorithms such as bonaparte validation or Java Bean Validation. Contentwise, this is a subset of the <code>PARAMETER_ERROR</code> range, but these codes will most likely be caught and mapped to more generic return codes, or used as user feedback in the UI. */
    static public final int VALIDATION_ERROR = 7;
    /** The classification of return codes indicating failure of an internal plausibility check. This should never happen and therefore usually indicates a programming error. */
    static public final int INTERNAL_LOGIC_ERROR = 8;  // assertion failed
    /** The classification of problems occurring in the persistence layer (usually database), which has not been caught by a specific exception handler. This can be due to resource exhaustion, but also programming errors. Usually deeper investigation is required. Callers receiving this code should retry at maximum one time, and then defer the request and queue it into a manual analysis queue. */
    static public final int DATABASE_ERROR = 9;


    /** The factor by which the classification code is multiplied. An error code modulus the classification factor gives details about where and why the problem occured. */
    static public final int CLASSIFICATION_FACTOR = 100000000;

    /** Provides the mapping of error codes to textual descriptions. It is the responsibility of superclasses
     *  inheriting this class to populate this map for the descriptions of the codes they represent.
     *  It is recommended to perform such initialization not during class load, but lazily, once the first exception is thrown.
     */
    static protected Map<Integer,String> codeToDescription = new HashMap<Integer, String>(200);

    private final int errorCode;

    /** Returns the error code for this exception */
    public final int getErrorCode() {
        return errorCode;
    }

    /** Creates a new ApplicationException for a given error code. */
    public ApplicationException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    /** Creates a new ApplicationException for a given error code, with some explanatory details. */
    public ApplicationException(int errorCode, String detailedMessage) {
        super("Code " + Integer.toString(errorCode) + (detailedMessage == null ? "" : " @ " + detailedMessage));
        this.errorCode = errorCode;
    }

    /** Returns the classification code for this exception. */
    public final int getClassification() {
        return (errorCode / CLASSIFICATION_FACTOR);
    }

    /** Returns information if a code is an "OK" code. */
    public static boolean isOk(int returnCode) {
        return returnCode >= 0 && returnCode < CLASSIFICATION_FACTOR;
    }
    
    /** returns a text representation of an error code, independent of an existing exception */
    public static String codeToString(int code) {
        String msg = codeToDescription.get(code);
        return msg != null ? msg : "unknown code";
    }
    
    /** Returns a textual description of the error code.
     *  The method is declared as final as long as it's used from the constructors of superclasses.
     *
     * @return the textual description.
     */
    public final String getStandardDescription() {
        return codeToString(errorCode);
    }

    /** Returns a textual description of the exception.
     *
     * @return the textual description.
     */
    @Override
    public String toString() {
        return super.toString() + ": (" + getStandardDescription() + ")";
    }

    /**
     * Creates a localized description of the standard message
     * Subclasses may override this method in order to produce a
     * locale-specific message.  For subclasses that do not override this
     * method, the default implementation returns the same result as
     * {@code getStandardMessage()}.
     *
     * @return  The localized description of this ApplicationException.
     */
    public String getLocalizedStandardDescription() {
        return getStandardDescription();
    }
}
