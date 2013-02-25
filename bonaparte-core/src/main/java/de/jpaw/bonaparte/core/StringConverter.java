package de.jpaw.bonaparte.core;

import java.util.List;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/**
 * The StringConverter interface is used to run some preprocessor over objects whih are mapped to type String (Ascii, Unicode, Upper, Lower).
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 */
public interface StringConverter {
    /** Method invoked for a single String */
    String convert(String oldValue, final AlphanumericElementaryDataItem meta);
    
    /** Method invoked for a String List. This can be used to alter the list length / add / remove elements after processing.
     * Please note that the converter is only invoked on the list itself, not on the individual elements. */
    List <String> convertList(List<String> oldList, final AlphanumericElementaryDataItem meta);

    /** Method invoked for an array of Strings. This can be used to alter the list length / add / remove elements after processing.
     * Please note that the converter is only invoked on the array itself, not on the individual elements. */
    String [] convertArray(String [] oldArray, final AlphanumericElementaryDataItem meta);

}
