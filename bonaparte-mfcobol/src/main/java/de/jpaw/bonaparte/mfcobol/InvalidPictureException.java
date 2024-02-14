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
package de.jpaw.bonaparte.mfcobol;

import de.jpaw.util.ApplicationException;

/**
 * The InvalidPictureException class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Extends the generic ApplicationException class in order to provide error details which are
 *          specific to validation of picture properties, used with the MF Cobol parser / composer.
 */

public class InvalidPictureException extends ApplicationException {
    private static final long serialVersionUID = 8530206162841742151L;

    private static final int ILE_OFFSET = CL_INTERNAL_LOGIC_ERROR * CLASSIFICATION_FACTOR + 13000;  // offset for all codes in this class

    public static final int NO_MATCH                 = ILE_OFFSET + 1;
    public static final int INVALID_GROUP_COUNT      = ILE_OFFSET + 2;
    public static final int POINT_WITH_PACKED        = ILE_OFFSET + 3;
    public static final int SIGN_WITH_PACKED         = ILE_OFFSET + 4;
    public static final int UNSUPPORTED_STORAGE_TYPE = ILE_OFFSET + 5;
    public static final int MISSING_PIC_PROPERTY     = ILE_OFFSET + 6;
    public static final int MISSING_PARSER           = ILE_OFFSET + 7;
    public static final int UNSUPPORTED_NUMBER_OF_DIGITS = ILE_OFFSET + 8;
    public static final int TYPE_NO_FRACTIONALS          = ILE_OFFSET + 9;

    static {
        registerRange(ILE_OFFSET, false, InvalidPictureException.class, ApplicationLevelType.CORE_LIBRARY);

        registerCode(NO_MATCH,                 "pic property does not match supported pattern");
        registerCode(INVALID_GROUP_COUNT,      "Pattern total group count not as expected - should never happen");
        registerCode(POINT_WITH_PACKED,        "Display style decimal point for COMP data types not allowed");
        registerCode(SIGN_WITH_PACKED,         "Display style sign for COMP data types not allowed");
        registerCode(UNSUPPORTED_STORAGE_TYPE, "Storage type parsed but not evaluated");
        registerCode(MISSING_PIC_PROPERTY,     "Missing pic property and no default provided");
        registerCode(MISSING_PARSER,           "No parser defined for the specified pic");
        registerCode(UNSUPPORTED_NUMBER_OF_DIGITS, "Currently max. 18 digits are supported");
        registerCode(TYPE_NO_FRACTIONALS,      "Type does not allow fractional digits");
    }

    public InvalidPictureException(int errorCode, String fieldName, String className, final Integer index) {
        super(errorCode, fieldName, className, index);
    }

    public InvalidPictureException(final int errorCode, final String fieldName, final String className) {
        super(errorCode, fieldName, className, null);
    }

    public InvalidPictureException(int errorCode) {
        super(errorCode);
    }
}
