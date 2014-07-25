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
package de.jpaw.bonaparte.core;

import de.jpaw.util.ApplicationException;

/**
 * The ObjectValidationException class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Extends the generic ApplicationException class in order to provide error details which are
 *          specific to validation of fields.
 */

public class ObjectValidationException extends ApplicationException {
    private static final long serialVersionUID = 8530206162841355351L;
//    private static final Logger logger = LoggerFactory.getLogger(MessageParserException.class);

    private static final int OFFSET = VALIDATION_ERROR * CLASSIFICATION_FACTOR + 17000;  // offset for all codes in this class

    private final String fieldName;   // if known, the name of the field where the error occurred
    private final String className;   // if known, the name of the class which contained the field

    static public final int MAY_NOT_BE_BLANK      = OFFSET + 1;
    static public final int NO_PATTERN_MATCH      = OFFSET + 2;
    static public final int TOO_MANY_ELEMENTS     = OFFSET + 3;
    static public final int TOO_LONG              = OFFSET + 4;
    static public final int TOO_SHORT             = OFFSET + 5;
    static public final int NOT_ENOUGH_ELEMENTS   = OFFSET + 6;
    static public final int NO_NEGATIVE_ALLOWED   = OFFSET + 7;
    
    // BigDecimal checks
    static public final int TOO_MANY_FRACTIONAL_DIGITS   = OFFSET + 10;
    static public final int TOO_MANY_DIGITS              = OFFSET + 11;

    static public final int NOT_FREEZABLE         = OFFSET + 21;
    static public final int OBJECT_IS_FROZEN      = OFFSET + 22;
    static public final int IS_IMMUTABLE          = OFFSET + 23;

    static {
        codeToDescription.put(MAY_NOT_BE_BLANK          , "Empty, but required field");
        codeToDescription.put(NO_PATTERN_MATCH          , "Field contents does not match required pattern");
        codeToDescription.put(TOO_MANY_ELEMENTS         , "Array contains too many elements");
        codeToDescription.put(TOO_LONG                  , "String is too long");
        codeToDescription.put(TOO_SHORT                 , "String is too short");
        codeToDescription.put(NOT_ENOUGH_ELEMENTS       , "Array contains not enough elements");
        codeToDescription.put(NO_NEGATIVE_ALLOWED       , "Number may not be negative");
            
        codeToDescription.put(TOO_MANY_FRACTIONAL_DIGITS, "Too many significant decimal digits");
        codeToDescription.put(TOO_MANY_DIGITS           , "Number too big");
            
        codeToDescription.put(NOT_FREEZABLE             , "This object cannot be turned into immutable state");
        codeToDescription.put(OBJECT_IS_FROZEN          , "Object instance is frozen and cannot be modified");
        codeToDescription.put(IS_IMMUTABLE              , "This object cannot be turned into mutable state");
    }

    private final String getSpecificDescription() {
        return (className == null ? "?" : className) + "."
             + (fieldName == null ? "?" : fieldName);
    }

    public ObjectValidationException(int errorCode, String fieldName, String className) {
        super(errorCode, null);
        this.fieldName = fieldName;
        this.className = className;
        // for the logger call, do NOT use toString, because that can be overridden, and we're called from a constructor here
        // do not log at all this one, log output is up to the application
        // logger.error("Error " + getErrorCode() + " (" + getStandardDescription() + ") for " + getSpecificDescription());
    }

    public ObjectValidationException(int errorCode) {
        this(errorCode, null, null);
    }

    @Override
    public String toString() {
        return getSpecificDescription() + ": " + super.toString();
    }
}
