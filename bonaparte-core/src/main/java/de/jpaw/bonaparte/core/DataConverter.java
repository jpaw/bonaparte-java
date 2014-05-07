package de.jpaw.bonaparte.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

/**
 * The DataConverter interface is used to run some preprocessor over objects which are mapped to a specific Java data type.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 */
public interface DataConverter<JAVATYPE,METATYPE extends FieldDefinition> {
    /** Method invoked for a single JAVATYPE */
    JAVATYPE convert(JAVATYPE oldValue, final METATYPE meta);

    /** Method invoked for a JAVATYPE List. This can be used to alter the list length / add / remove elements after processing.
     * Please note that the converter is only invoked on the list itself, not on the individual elements. */
    List <JAVATYPE> convertList(List<JAVATYPE> oldList, final METATYPE meta);

    /** Method invoked for a JAVATYPE Set. This can be used to alter the set size / add / remove elements after processing.
     * Please note that the converter is only invoked on the list itself, not on the individual elements. */
    Set <JAVATYPE> convertSet(Set<JAVATYPE> oldSet, final METATYPE meta);

    /** Method invoked for an array of JAVATYPEs. This can be used to alter the list length / add / remove elements after processing.
     * Please note that the converter is only invoked on the array itself, not on the individual elements. */
    JAVATYPE [] convertArray(JAVATYPE [] oldArray, final METATYPE meta);

    /** Map-type methods. The tree walker converses the value parts of the map only. */
    public <K> Map<K, JAVATYPE> convertMap(Map<K, JAVATYPE> oldMap, final METATYPE meta);
}
