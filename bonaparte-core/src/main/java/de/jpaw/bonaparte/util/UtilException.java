 /*
  * Copyright 2014 Michael Bischoff
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
package de.jpaw.bonaparte.util;

import de.jpaw.util.ApplicationException;

/**
 * The UtilException class.
 *
 * @author Michael Bischoff
 *
 *          Extends the generic ApplicationException class in order to provide error details which are
 *          specific to some utility methods.
 */

public class UtilException extends ApplicationException {
    private static final long serialVersionUID = 85302061628413351L;

    private static final int OFFSET = CL_VALIDATION_ERROR * CLASSIFICATION_FACTOR + 15000;  // offset for all codes in this class

    public static final int PATH_COMPONENT_NOT_FOUND    = OFFSET + 1;
    public static final int DESCEND_TO_NON_REFERENCE    = OFFSET + 2;
    public static final int DESCEND_TO_GENERIC_OBJECT   = OFFSET + 3;
    public static final int ADAPTER_WITHOUT_FIELDS      = OFFSET + 4;
    public static final int NO_CLOSING_BRACKET          = OFFSET + 5;

    static {
        registerRange(OFFSET, false, UtilException.class, ApplicationLevelType.CORE_LIBRARY, "Basic utility library");

        registerCode(PATH_COMPONENT_NOT_FOUND      , "Path component not found");
        registerCode(DESCEND_TO_NON_REFERENCE      , "Path element is not the last one but is no reference");
        registerCode(DESCEND_TO_GENERIC_OBJECT     , "Path element leads to a generic reference");
        registerCode(ADAPTER_WITHOUT_FIELDS        , "Adapter defines no field (should not happen)");
        registerCode(NO_CLOSING_BRACKET            , "Unmatched opening bracket");
    }

    public UtilException(int errorCode, String text) {
        super(errorCode, text);
    }

    public UtilException(int errorCode) {
        this(errorCode, null);
    }
}
