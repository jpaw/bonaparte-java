package de.jpaw.bonaparte.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.ParsedFoldingComponent;

/** Delegates most output to the delegateComposer, but uses a permutation/selection of fields for the object output. */
public class FoldingComposer<E extends Exception> extends DelegatingBaseComposer<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoldingComposer.class);
    public static final Map<Class<? extends BonaCustom>, List<String>> EMPTY_MAPPING = ImmutableMap.of();

    private final Map<Class<? extends BonaCustom>, List<String>> mapping;
    private final Map<Class<? extends BonaCustom>, List<ParsedFoldingComponent>> parsedMapping;
    private final FoldingStrategy errorStrategy;
    private final List<String> bonaPortableMapping;
    private final List<String> bonaCustomMapping;

    public FoldingComposer(MessageComposer<E> delegateComposer, Map<Class<? extends BonaCustom>, List<String>> mapping, FoldingStrategy errorStrategy) {
        super(delegateComposer);
        this.mapping = mapping;
        this.parsedMapping = new HashMap<Class<? extends BonaCustom>, List<ParsedFoldingComponent>>(32);
        this.errorStrategy = errorStrategy;
        this.bonaPortableMapping = mapping.get(BonaPortable.class);
        this.bonaCustomMapping = mapping.get(BonaCustom.class);
    }

    /** Convenience method to write a list of fieldnames to some output. */
    public static <X extends Exception> void writeFieldsToDelegate(MessageComposer<X> writer, BonaCustom obj, List<String> fieldnames) throws X {
        Map<Class<? extends BonaCustom>, List<String>> mapping = Collections.<Class<? extends BonaCustom>, List<String>>singletonMap(BonaPortable.class, fieldnames);
        new FoldingComposer<X>(writer, mapping, FoldingStrategy.FORWARD_OBJECTS).writeRecord(obj);
    }


    @Override
    public void writeSuperclassSeparator() throws E {
        // delegateComposer.writeSuperclassSeparator();   // the folded structure is flat
    }


    private List<ParsedFoldingComponent> createParsedFieldList(ObjectReference di, BonaCustom obj, Class <? extends BonaCustom> objClass) throws E {
        // get the original mapping...

        // if only one mapping entry has been provided, and that is for a BonaCustom in general, this is straightforward.

        List<String> fieldList = mapping.get(objClass);
        if (fieldList == null) {
            switch (errorStrategy) {
            case SKIP_UNMAPPED:
                return null;
            case FULL_OUTPUT:
                delegateComposer.startObject(di, obj);
                obj.serializeSub(this); // this or delegateComposer?
                delegateComposer.terminateObject(di, obj);
                return null;
            case TRY_SUPERCLASS:
            case SUPERCLASS_OR_FULL:
            case FORWARD_OBJECTS:
                Class <?> superclass;
                while ((superclass = objClass.getSuperclass()) != null) {
                    if (BonaCustom.class.isAssignableFrom(superclass)) {
                        objClass = (Class<? extends BonaCustom>)superclass;
                        fieldList = mapping.get(objClass);
                        if (fieldList != null) {
                            LOGGER.debug("Mapping for class {} found at superclass {}",
                                    obj.getClass().getCanonicalName(),
                                    objClass.getCanonicalName());
                            break;
                        }
                    } else {
                        if (bonaPortableMapping != null) {
                            fieldList = bonaPortableMapping;
                        } else if (bonaCustomMapping != null) {
                            fieldList = bonaCustomMapping;
                        } else {
                            if (errorStrategy != FoldingStrategy.TRY_SUPERCLASS) {
                                // all others default to "full output"
                                delegateComposer.startObject(di, obj);
                                obj.serializeSub(this); // this or delegateComposer?
                                delegateComposer.terminateObject(di, obj);
                            }
                            return null;  // skip, no mapping found even with recursion
                        }
                        break;
                    }
                }
            }
        }
        // if fieldList is still null, then we must have reached the top class (superclass == null)
        if (fieldList == null)
            fieldList = bonaPortableMapping;   // fallback tpo avoid NPE below...
        // fieldList is not null now.
        // parse it
        List<ParsedFoldingComponent> pl = new ArrayList<ParsedFoldingComponent>(fieldList.size());
        for (String f: fieldList) {
            // create an entry in pl
            pl.add(createRecursiveFoldingComponent(f));
        }
        parsedMapping.put(obj.getClass(), pl);
        LOGGER.debug("Created parsed mapping for class {}", obj.getClass().getCanonicalName());
        return pl;
    }

    public static ParsedFoldingComponent createRecursiveFoldingComponent(String f) {
        ParsedFoldingComponent pfc = new ParsedFoldingComponent();
        int dotIndex = f.indexOf('.');
        if (dotIndex < 0) {
            pfc.setFieldname(f);
            pfc.setComponent(null);
        } else {
            pfc.setFieldname(f.substring(0, dotIndex));
            pfc.setComponent(createRecursiveFoldingComponent(f.substring(dotIndex+1)));
        }
        // parse possible indexes, numeric or alphanumeric
        pfc.setIndex(-1);  // default: all nont-existing
        dotIndex = pfc.getFieldname().indexOf('[');
        if (dotIndex >= 0) {
            String indexStr = pfc.getFieldname().substring(dotIndex+1);
            pfc.setFieldname(pfc.getFieldname().substring(0, dotIndex));
            dotIndex = indexStr.indexOf(']');
            if (dotIndex != indexStr.length()-1) {
                LOGGER.error("Unparseable index for field {}: [{}], ignored", pfc.getFieldname(), indexStr);
                return pfc;
            }
            indexStr = indexStr.substring(0, dotIndex);
            pfc.setAlphaIndex(indexStr);
            // try to parse a numeric index
            if (Character.isDigit(indexStr.charAt(0))) {
                try {
                    pfc.setIndex(Integer.parseInt(indexStr));
                } catch (Exception e) {
                    LOGGER.error("Cannot parse numeric index for field {}: [{}], ignored", pfc.getFieldname(), indexStr);
                    return pfc;
                }
            }
        }
        // pfc.setNumDescends(-1);
        return pfc;
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) throws E {
        if (obj == null) {
            writeNull(di);
        } else {
            final boolean isGenericObj = di.getProperties() != null && di.getProperties().containsKey("genericobject");
            if (isGenericObj || (errorStrategy == FoldingStrategy.FORWARD_OBJECTS && di != StaticMeta.OUTER_BONAPORTABLE)) {
                // purpose is to output objects as they are
                delegateComposer.addField(di, obj);
                return;
            }
            // only write the fields selectively
            // first, optionally create a cached mapping
            Class <? extends BonaCustom> objClass = obj.getClass();
            List<ParsedFoldingComponent> parsedFieldList = parsedMapping.get(objClass);
            if (parsedFieldList == null) {
                parsedFieldList = createParsedFieldList(di, obj, objClass);
                if (parsedFieldList == null)
                    return;
            }
            // some fieldList has been found if we end up here
            // now perform the output, used the parsed list
            startObject(di, obj);
            for (ParsedFoldingComponent pfc: parsedFieldList)
                obj.foldedOutput(this, pfc);
            terminateObject(di, obj);
        }
    }
}
