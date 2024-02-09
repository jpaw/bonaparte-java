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

    private static final int OFFSET = CL_VALIDATION_ERROR * CLASSIFICATION_FACTOR + 16000;  // offset for all codes in this class
    private static final int ILE_OFFSET = CL_INTERNAL_LOGIC_ERROR * CLASSIFICATION_FACTOR + 16000;  // offset for all codes in this class

    public static final int MAY_NOT_BE_BLANK            = OFFSET + 1;
    public static final int NO_PATTERN_MATCH            = OFFSET + 2;
    public static final int TOO_MANY_ELEMENTS           = OFFSET + 3;
    public static final int TOO_LONG                    = OFFSET + 4;
    public static final int TOO_SHORT                   = OFFSET + 5;
    public static final int NOT_ENOUGH_ELEMENTS         = OFFSET + 6;
    public static final int NO_NEGATIVE_ALLOWED         = OFFSET + 7;
    public static final int CUSTOM_VALIDATION           = OFFSET + 8;
    public static final int NO_ACTIVE_FIELD             = OFFSET + 9;

    // BigDecimal checks
    public static final int TOO_MANY_FRACTIONAL_DIGITS  = OFFSET + 10;
    public static final int TOO_MANY_DIGITS             = OFFSET + 11;

    public static final int NOT_FREEZABLE               = OFFSET + 21;
    public static final int OBJECT_IS_FROZEN            = OFFSET + 22;
    public static final int IS_IMMUTABLE                = OFFSET + 23;

    // JSON composer codes
    public static final int UNSUPPORTED_MAP_KEY_TYPE    = ILE_OFFSET + 51;
    public static final int UNSUPPORTED_MAP_VALUE_TYPE  = ILE_OFFSET + 52;
    public static final int INVALID_SEQUENCE            = ILE_OFFSET + 53;
    public static final int MAP_NOT_UNSUPPORTED         = ILE_OFFSET + 54;

    static {
        registerCode(MAY_NOT_BE_BLANK          , "Empty, but required field");
        registerCode(NO_PATTERN_MATCH          , "Field contents does not match required pattern");
        registerCode(TOO_MANY_ELEMENTS         , "Array contains too many elements");
        registerCode(TOO_LONG                  , "String is too long");
        registerCode(TOO_SHORT                 , "String is too short");
        registerCode(NOT_ENOUGH_ELEMENTS       , "Array contains not enough elements");
        registerCode(NO_NEGATIVE_ALLOWED       , "Number may not be negative");
        registerCode(CUSTOM_VALIDATION         , "A custom validation has failed");
        registerCode(NO_ACTIVE_FIELD           , "Cannot alter the active state for a class without an active field");

        registerCode(TOO_MANY_FRACTIONAL_DIGITS, "Too many significant decimal digits");
        registerCode(TOO_MANY_DIGITS           , "Number too big");

        registerCode(NOT_FREEZABLE             , "This object cannot be turned into immutable state");
        registerCode(OBJECT_IS_FROZEN          , "Object instance is frozen and cannot be modified");
        registerCode(IS_IMMUTABLE              , "This object cannot be turned into mutable state");

        // JSON map codes
        registerCode(UNSUPPORTED_MAP_KEY_TYPE  , "This map key type is currently not supported");
        registerCode(UNSUPPORTED_MAP_VALUE_TYPE, "This map key type is currently not supported");
        registerCode(INVALID_SEQUENCE          , "Invalid sequence of map output");
        registerCode(MAP_NOT_UNSUPPORTED       , "A map data type is not supported for this composer");
    }

    public ObjectValidationException(int errorCode, String fieldName, String className) {
        super(errorCode, fieldName, className, null);
    }

    public ObjectValidationException(int errorCode) {
        super(errorCode);
    }
}
