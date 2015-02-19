package de.jpaw.bonaparte.refsp;

import de.jpaw.bonaparte.pojos.apip.Ref;

/** Interface to determine a computed long number for a given reference.
 * 
 * Examples of use are:
 * - concatenation of keys for natural keys, if the components are small numbers.
 * - Conversion of short strings into numbers
 * - use of lookup tables
 * - any perfect hash
 *  */
public interface KeyCreator<REF extends Ref> {

    /** Computes a key for a given reference object, or returns 0L if the reference object is not suitable to create one,
     * in which case other strategies must be evaluated. */
    long computeKey(REF ref);
}
