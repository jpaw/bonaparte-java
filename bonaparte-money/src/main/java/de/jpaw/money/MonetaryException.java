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
package de.jpaw.money;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.util.ApplicationException;

/**
 * The MonetaryException class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Extends the generic ApplicationException class in order to provide error details which are
 *          specific to currency conversion and monetary rounding.
 */

public class MonetaryException extends ApplicationException {
    /**
     * 
     */
    private static final long serialVersionUID = 5464727960916479900L;

    private static final Logger logger = LoggerFactory.getLogger(MonetaryException.class);

    private static final int OFFSET = (PARAMETER_ERROR * CLASSIFICATION_FACTOR) + 18000; // offset for all codes in this class
    private static final int OFFSET_ILE = (INTERNAL_LOGIC_ERROR * CLASSIFICATION_FACTOR) + 18000; // offset for all codes in this class
    private static boolean textsInitialized = false;

    static public final int ILLEGAL_CURRENCY_CODE        = OFFSET + 1;
    static public final int ILLEGAL_NUMBER_OF_DECIMALS   = OFFSET + 2;
    static public final int UNDEFINED_AMOUNTS            = OFFSET + 3;
    static public final int TAX_EXCEED_GROSS             = OFFSET + 4;
    static public final int SUM_MISMATCH                 = OFFSET + 5;
    static public final int ROUNDING_PROBLEM             = OFFSET + 6;
    static public final int SIGNS_DIFFER                 = OFFSET + 7;
    static public final int INCOMPATIBLE_OPERANDS        = OFFSET + 8;
    static public final int INCORRECT_NUMBER_TAX_AMOUNTS = OFFSET + 9;

    static public final int UNEXPECTED_ROUNDING_PROBLEM  = OFFSET_ILE + 21;

    /**
     * Method lazyInitialization.
     * 
     * Upload textual descriptions only once they're needed for this type of exception class.
     * The idea is that in working environments, we will never need them ;-).
     * There is a small chance of duplicate initialization, because the access to the flag textsInitialized is not
     * synchronized, but duplicate upload does not hurt (is idempotent)
     */
    static private void lazyInitialization() {
        synchronized (codeToDescription) {
            textsInitialized = true;
            codeToDescription.put(ILLEGAL_CURRENCY_CODE        , "Currency code may not be null, must have 3 upper case characters length");
            codeToDescription.put(ILLEGAL_NUMBER_OF_DECIMALS   , "The number of fractional digits must be between 0 and 6");
            codeToDescription.put(UNDEFINED_AMOUNTS            , "Both gross and net amounts were null");
            codeToDescription.put(TAX_EXCEED_GROSS             , "Tax amounts exceed gross amount");
            codeToDescription.put(SUM_MISMATCH                 , "Gross, net and tax amounts provided, but the sum of net and tax does not match gross");
            codeToDescription.put(ROUNDING_PROBLEM             , "Problem during rounding (precision loss due to too many provided decimal digits)");
            codeToDescription.put(SIGNS_DIFFER                 , "The signs of tax and net amount are not consistent");
            codeToDescription.put(UNEXPECTED_ROUNDING_PROBLEM  , "Unexpected exception from constructor");
            codeToDescription.put(INCOMPATIBLE_OPERANDS        , "The operands differ in either currency or number of tax amounts");
            codeToDescription.put(INCORRECT_NUMBER_TAX_AMOUNTS , "Incorrect number of tax amounts supplied");
        }
    }

    private final void constructorSubroutine() {
        if (!textsInitialized) {
            lazyInitialization();
        }
        // for the logger call, do NOT use toString, because that can be overridden, and we're called from a constructor here
        logger.error("Error " + getErrorCode() + " (" + getStandardDescription() + ")");
    }

    public MonetaryException(int errorCode, String message) {
        super(errorCode, message);
        constructorSubroutine();
    }

    public MonetaryException(int errorCode) {
        super(errorCode, null);
        constructorSubroutine();
    }
}
