package de.jpaw.bonaparte.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.bonaparte.core.DataAndMeta;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.core.ListComposer;
import de.jpaw.bonaparte.core.ListMetaComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.ExternalClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.ParsedFoldingComponent;

/** Utility method to get a fields from BonaPortables via pathname. */
public class FieldGetter {
    private static final Logger LOG = LoggerFactory.getLogger(FieldGetter.class);

    // configuration constant
    final private static boolean DEFAULT_AUTOSKIP_ADAPTERS = true;      // do not return adapter classes but their contents

    /** Retrieves the class instance for the previously static methods, the BClass.getInstance, for a given class.
     * Throws an exception if the provided class is not a BonaPortable or the BClass cannot be obtained. */
    public static BonaPortableClass<?> getBClass(Class<?> bonaPortableClass) {
        if (!BonaPortable.class.isAssignableFrom(bonaPortableClass)) {
            LOG.error(bonaPortableClass.getCanonicalName() + " is not a BonaPortable");
            throw new IllegalArgumentException();
        }
        try {
            // new method since bonaparte-3.2.4
            Method method = bonaPortableClass.getMethod("class$BonaPortableClass");
            return (BonaPortableClass<?>)method.invoke(null);
            // removed more complicated lookup of 3.2.3
//            Class<?> [] innerClasses = bonaPortableClass.getDeclaredClasses();
//            for (int i = 0; i < innerClasses.length; ++i) {
//                if (BonaPortableClass.class.isAssignableFrom(innerClasses[i])) {
//                    Method method = innerClasses[i].getMethod("getInstance");
//                    return (BonaPortableClass<?>)method.invoke(null);
//                }
//            }
//            LOG.error(bonaPortableClass.getCanonicalName() + " does not define an inner BonaPortable class");
//            throw new IllegalArgumentException();
        } catch (Exception e) {
            LOG.error(bonaPortableClass.getCanonicalName() + " failed to return its BonaPortableClass via reflection: ", e);
            throw new RuntimeException(e);
        }
    }



    public static List<Object> getFields(BonaPortable obj, List<String> fieldnames) {
        return getFieldsOrObjects(obj, fieldnames, false, !DEFAULT_AUTOSKIP_ADAPTERS);
    }

    /** Get a single field, reusing the get multiple implementation. */
    public static Object getField(BonaPortable obj, String fieldname) {
        List<Object> target = getFields(obj, Collections.singletonList(fieldname));     // the list with the result
        return (target.size() == 0) ? null : target.get(0);
    }

    public static List<Object> getFieldsOrObjects(BonaPortable obj, List<String> fieldnames) {
        return getFieldsOrObjects(obj, fieldnames, true, !DEFAULT_AUTOSKIP_ADAPTERS);
    }

    public static List<Object> getFieldsOrObjects(BonaPortable obj, List<String> fieldnames, boolean keepObjects, boolean keepExternals) {
        if (obj == null || fieldnames == null)
            return null;

        // step 1: construct the output buffer
        List<Object> target = new ArrayList<Object>(fieldnames.size());     // the list to write the field into

        // step 2: create and chain the message composers
        ListComposer writer = new ListComposer(target, false, keepObjects, keepExternals);
        FoldingComposer.writeFieldsToDelegate(writer, obj, fieldnames);

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
        ListComposer delegate = new ListComposer(target, false, true, !DEFAULT_AUTOSKIP_ADAPTERS);
        obj.foldedOutput(delegate, pfc);
        return (target.size() == 0) ? null : target.get(0);
    }

    /** Get a single field, alternate implementation, going directly to ListComposer (should be faster, less object allocations). */
    public static DataAndMeta getSingleFieldWithMeta(BonaPortable obj, String fieldname) {
        ParsedFoldingComponent pfc = FoldingComposer.createRecursiveFoldingComponent(fieldname);
        List<DataAndMeta> target = new ArrayList<DataAndMeta>(1);
        ListMetaComposer delegate = new ListMetaComposer(target, false, false, !DEFAULT_AUTOSKIP_ADAPTERS);
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

    /** Returns the first field defined in a superclass or this class, or null, if neither this class nor a superclass defines a field. */
    public static FieldDefinition getFirstField(ClassDefinition cls) {
        FieldDefinition f = cls.getParentMeta() == null ? null : getFirstField(cls.getParentMeta());
        if (f != null)
            return f;  // found a field in a parent
        if (cls.getFields().size() == 0)
            return null;
        return cls.getFields().get(0);
    }

    /** Retrieves just the meta data. Throws an exception if the path is invalid.
     * This is a static path resolver, pathnames must reference valid paths for any valid data. */
    public static FieldDefinition getFieldDefinitionForPathname(ClassDefinition cls, String pathname) throws UtilException {
        return getFieldDefinitionForPathname(cls, pathname, DEFAULT_AUTOSKIP_ADAPTERS);
    }

    /** Retrieves just the meta data. Throws an exception if the path is invalid.
     * This is a static path resolver, pathnames must reference valid paths for any valid data. */
    public static FieldDefinition getFieldDefinitionForPathname(ClassDefinition cls, String pathname, final boolean autoSkipAdapters) throws UtilException {

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
                throw new UtilException(UtilException.PATH_COMPONENT_NOT_FOUND, currentElement + " in " + cls.ret$PQON());

            // check for adapters...
            ObjectReference oRef;
            if (autoSkipAdapters) {
                for (;;) {
                    oRef = fld instanceof ObjectReference ? (ObjectReference)fld : null;
                    if (oRef != null && oRef.getLowerBound() != null && oRef.getLowerBound() instanceof ExternalClassDefinition) {
                        ExternalClassDefinition extRef = (ExternalClassDefinition)oRef.getLowerBound();
                        if (extRef.getIsSingleField()) {
                            // forward fld to the first fld of the adapter...
                            fld = getFirstField(extRef);
                            if (fld == null)
                                throw new UtilException(UtilException.ADAPTER_WITHOUT_FIELDS, extRef.getClassRef().getCanonicalName());
                            continue;   // iterative check for adapters, in case of nested adapters...
                        }
                    }
                    break;      // any condition not fulfilled: stop iterating
                }
            } else {
                oRef = fld instanceof ObjectReference ? (ObjectReference)fld : null; // just a one time evaluation
            }

            final boolean isGenericObjectField = cls.getProperties() != null && cls.getProperties().containsKey(currentElement + ".genericobject");
            if (lastDot < 0 || isGenericObjectField)
                // this was the result
                return fld;
            // otherwise, must descend further. For that, fld must be a class reference
            if (oRef == null)
                throw new UtilException(UtilException.DESCEND_TO_NON_REFERENCE, currentElement + " in " + cls.ret$PQON());
            if (oRef.getLowerBound() == null)
                throw new UtilException(UtilException.DESCEND_TO_GENERIC_OBJECT, currentElement + " in " + cls.ret$PQON());
            cls = oRef.getSecondaryLowerBound() == null ? oRef.getLowerBound() : oRef.getSecondaryLowerBound();
            // continue...
        }
    }
}
