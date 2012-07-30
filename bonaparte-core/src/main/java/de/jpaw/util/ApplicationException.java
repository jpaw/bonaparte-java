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
 * The ApplicationException class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          This class defines the parent class for all application errors which are
 *          thrown in the marshalling / unmarshalling or messaging areas, as well as
 *          the application modules themselves.
 */

public class ApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1122421467960337766L;
	/**
	 * 
	 */
	// the 8th digit of an error code gives the high level cause / return code
	static public final int SUCCESS = 0;
	static public final int DENIED  = 1;
	static public final int PARSER_ERROR = 2;
	static public final int PARAMETER_ERROR = 3;
	static public final int TIMEOUT = 4;
	// ... room for more...
	static public final int VALIDATION_ERROR = 7;
	static public final int INTERNAL_LOGIC_ERROR = 8;  // assertion failed
	static public final int DATABASE_ERROR = 9;
	static public final int CLASSIFICATION_FACTOR = 100000000;

	// this map will be filled by superclasses
	static protected Map<Integer,String> codeToDescription = new HashMap<Integer, String>(200);
	
	private final int errorCode;

	public final int getErrorCode() {
		return errorCode;
	}
	
	public ApplicationException(int errorCode) {
        super();
		this.errorCode = errorCode;
	}
	
	public ApplicationException(int errorCode, String detailedMessage) {
        super(detailedMessage);
		this.errorCode = errorCode;
	}
	
	public final int getClassification() {
		return (errorCode / CLASSIFICATION_FACTOR) % 10;
	}

	// return a textual description of the error code
	// must be final as long as it's used from the constructors of superclasses
	public final String getStandardDescription() {
		String msg;
		synchronized(codeToDescription) {
			msg = codeToDescription.get(errorCode);
		}
		return msg != null ? msg : "unknown error code";
	}
	
	@Override
	public String toString() {
		return "Error code " + errorCode + " (" + getStandardDescription() + "): " + super.toString();
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
