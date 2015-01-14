package de.jpaw.bonaparte.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.DataAndMeta;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.core.ListComposer;
import de.jpaw.bonaparte.core.ListMetaComposer;
import de.jpaw.bonaparte.core.ListObjComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.ParsedFoldingComponent;

/** Utility method to get a fields from BonaPortables via pathname. */
public class FieldGetter {
    public static List<Object> getFields(BonaPortable obj, List<String> fieldnames) {
        if (obj == null || fieldnames == null)
            return null;
        
        // step 1: construct the output buffer
        List<Object> target = new ArrayList<Object>(fieldnames.size());     // the list to write the field into
        
        // step 2: create and chain the message composers
        ListComposer writer = new ListComposer(target, false);
        Map<Class<? extends BonaCustom>, List<String>> mapping = Collections.<Class<? extends BonaCustom>, List<String>>singletonMap(BonaPortable.class, fieldnames); 
        new FoldingComposer<RuntimeException>(writer, mapping, FoldingStrategy.FORWARD_OBJECTS).writeRecord(obj);

        return target;
    }
    
    /** Get a single field, reusing the get multiple implementation. */
    public static Object getField(BonaPortable obj, String fieldname) {
        List<Object> target = getFields(obj, Collections.singletonList(fieldname));     // the list with the result
        return (target.size() == 0) ? null : target.get(0);
    }

    public static List<Object> getFieldsOrObjects(BonaPortable obj, List<String> fieldnames) {
        if (obj == null || fieldnames == null)
            return null;
        
        // step 1: construct the output buffer
        List<Object> target = new ArrayList<Object>(fieldnames.size());     // the list to write the field into
        
        // step 2: create and chain the message composers
        ListComposer writer = new ListObjComposer(target, false);
        Map<Class<? extends BonaCustom>, List<String>> mapping = Collections.<Class<? extends BonaCustom>, List<String>>singletonMap(BonaPortable.class, fieldnames); 
        new FoldingComposer<RuntimeException>(writer, mapping, FoldingStrategy.FORWARD_OBJECTS).writeRecord(obj);

        return target;
    }
    
    /** Get a single field, reusing the get multiple implementation. */
    public static Object getFieldOrObj(BonaPortable obj, String fieldname) {
        List<Object> target = getFieldsOrObjects(obj, Collections.singletonList(fieldname));        // the list with the result
        return (target.size() == 0) ? null : target.get(0);
    }

    
    /** Get a single field, alternate implementation, going directly to ListComposer (should be faster, less object allocations). */
    public static Object getSingleField(BonaPortable obj, String fieldname) {
        ParsedFoldingComponent pfc = FoldingComposer.createRecursiveFoldingComponent(fieldname);
        List<Object> target = new ArrayList<Object>(1);
        ListComposer delegate = new ListComposer(target, false);
        obj.foldedOutput(delegate, pfc);
        return (target.size() == 0) ? null : target.get(0);
    }

    /** Get a single field, alternate implementation, going directly to ListComposer (should be faster, less object allocations). */
    public static DataAndMeta<Object,FieldDefinition> getSingleFieldWithMeta(BonaPortable obj, String fieldname) {
        ParsedFoldingComponent pfc = FoldingComposer.createRecursiveFoldingComponent(fieldname);
        List<DataAndMeta<Object,FieldDefinition>> target = new ArrayList<DataAndMeta<Object,FieldDefinition>>(1);
        ListMetaComposer delegate = new ListMetaComposer(target, false);
        obj.foldedOutput(delegate, pfc);
        return (target.size() == 0) ? null : target.get(0);
    }
    
    /** Finds a field in a class or its parent. Returns null if not found. */
    public static FieldDefinition lookupField(ClassDefinition cls, String name) {
        while (cls != null) {
            // find in this class
            for (FieldDefinition f : cls.getFields())
                if (f.getName().equals(name))
                    return f;
            // not found here, maybe in parent?
            cls = cls.getParentMeta();
        }
        // no more parent
        return null;
    }
    
    /** Retrieves just the meta data. Throws an exception if the path is invalid.
     * This is a static path resolver, pathnames must reference valid paths for any valid data. */
    public static FieldDefinition getFieldDefinitionForPathname(ClassDefinition cls, String pathname) throws UtilException {

        // if the pathname contains no dot, the only containing object is the root
        for (;;) {
            String currentElement;
            final int lastDot = pathname.indexOf('.');
            if (lastDot < 0) {        
                // this was the last component
                currentElement = pathname;
            } else {
                currentElement = pathname.substring(0, lastDot);
                pathname = pathname.substring(lastDot+1);
            }
            // exclude any array or map index
            final int bracketPos = currentElement.indexOf('[');
            if (bracketPos >= 0)
                currentElement = currentElement.substring(0, bracketPos);
            // now look up the name in the field list
            FieldDefinition fld = lookupField(cls, currentElement);
            if (fld == null)
                throw new UtilException(UtilException.PATH_COMPONENT_NOT_FOUND, currentElement + " in " + cls.get$PQON());
            if (lastDot < 0)
                // this was the result
                return fld;
            // otherwise, must descend further. For that, fld must be a class reference
            if (!(fld instanceof ObjectReference))
                throw new UtilException(UtilException.DESCEND_TO_NON_REFERENCE, currentElement + " in " + cls.get$PQON());
            ObjectReference oRef = (ObjectReference)fld;
            if (oRef.getLowerBound() == null)
                throw new UtilException(UtilException.DESCEND_TO_GENERIC_OBJECT, currentElement + " in " + cls.get$PQON());
            cls = oRef.getSecondaryLowerBound() == null ? oRef.getLowerBound() : oRef.getSecondaryLowerBound();
            // continue...
        }
    }
}
