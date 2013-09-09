package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ParsedFoldingComponent;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.util.ByteArray;

/** Delegates most output to the delegateComposer, but uses a permutation/selection of fields for the object output. */ 
public class FoldingComposer<E extends Exception> implements MessageComposer<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoldingComposer.class);
    private final MessageComposer<E> delegateComposer; 
    private final Map<Class<? extends BonaPortable>, List<String>> mapping;
    private final Map<Class<? extends BonaPortable>, List<ParsedFoldingComponent>> parsedMapping;
    private final FoldingStrategy errorStrategy;
    
    public FoldingComposer(MessageComposer<E> delegateComposer, Map<Class<? extends BonaPortable>, List<String>> mapping, FoldingStrategy errorStrategy) {
        this.delegateComposer = delegateComposer;
        this.mapping = mapping;
        this.parsedMapping = new HashMap<Class<? extends BonaPortable>, List<ParsedFoldingComponent>>(20);
        this.errorStrategy = errorStrategy;
    }

    @Override
    public void writeNull(FieldDefinition di) throws E {
        delegateComposer.writeNull(di);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws E {
        delegateComposer.writeNullCollection(di);
    }
    
    @Override
    public void startTransmission() throws E {
        delegateComposer.startTransmission();
    }

    @Override
    public void startRecord() throws E {
        delegateComposer.startRecord();
    }

    @Override
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) throws E {
        delegateComposer.startArray(currentMembers, maxMembers, sizeOfElement);
    }

    @Override
    public void startMap(int currentMembers, int indexID) throws E {
        delegateComposer.startMap(currentMembers, indexID);
    }

    @Override
    public void writeSuperclassSeparator() throws E {
        // delegateComposer.writeSuperclassSeparator();   // the folded structure is flat
    }

    @Override
    public void terminateMap() throws E {
        delegateComposer.terminateMap();
    }

    @Override
    public void terminateArray() throws E {
        delegateComposer.terminateArray();
    }

    @Override
    public void terminateRecord() throws E {
        delegateComposer.terminateRecord();
    }

    @Override
    public void terminateTransmission() throws E {
        delegateComposer.terminateTransmission();
    }

    @Override
    public void writeRecord(BonaPortable o) throws E {
        startRecord();
        addField(o);
        terminateRecord();
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws E {
        delegateComposer.addField(di, s);
    }

    @Override
    public void addField(boolean b) throws E {
        delegateComposer.addField(b);
    }

    @Override
    public void addField(char c) throws E {
        delegateComposer.addField(c);
    }

    @Override
    public void addField(double d) throws E {
        delegateComposer.addField(d);
    }

    @Override
    public void addField(float f) throws E {
        delegateComposer.addField(f);
    }

    @Override
    public void addField(byte n) throws E {
        delegateComposer.addField(n);
    }

    @Override
    public void addField(short n) throws E {
        delegateComposer.addField(n);
    }

    @Override
    public void addField(int n) throws E {
        delegateComposer.addField(n);
    }

    @Override
    public void addField(long n) throws E {
        delegateComposer.addField(n);
    }

    @Override
    public void addField(NumericElementaryDataItem di, Integer n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws E {
        delegateComposer.addField(di, b);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws E {
        delegateComposer.addField(di, b);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Calendar t) throws E {
        delegateComposer.addField(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws E {
        delegateComposer.addField(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws E {
        delegateComposer.addField(di, t);
    }
    
    @Override
    public void startObject(BonaPortable obj) throws E {
        delegateComposer.startObject(obj);
    }
    
    private List<ParsedFoldingComponent> createParsedFieldList(BonaPortable obj, Class <? extends BonaPortable> objClass) throws E {
        // get the original mapping...
        List<String> fieldList = mapping.get(objClass);
        if (fieldList == null) {
            switch (errorStrategy) {
            case SKIP_UNMAPPED:
                return null;
            case FULL_OUTPUT:
                delegateComposer.startObject(obj);
                obj.serializeSub(this); // this or delegateComposer?
                return null;
            case TRY_SUPERCLASS:
                Class <?> superclass;
                while ((superclass = objClass.getSuperclass()) != null) {
                    if (BonaPortable.class.isAssignableFrom(superclass)) {
                        objClass = (Class<? extends BonaPortable>)superclass;
                        fieldList = mapping.get(objClass);
                        if (fieldList != null) {
                            LOGGER.debug("Mapping for class {} found at superclass {}",
                                    obj.getClass().getCanonicalName(),
                                    objClass.getCanonicalName());
                            break;
                        }
                    } else {
                        return null;  // skip, no mapping found even with recursion
                    }
                    
                }
            }
        }
        // fieldList is not null now.
        // parse it
        List<ParsedFoldingComponent> pl = new ArrayList<ParsedFoldingComponent>(fieldList.size());
        for (String f: fieldList) {
            // create an entry in pl
            pl.add(createRecursiveFoldingComponent(obj.getClass(), f));
        }
        parsedMapping.put(obj.getClass(), pl);
        LOGGER.debug("Created parsed mapping for class {}", obj.getClass().getCanonicalName());
        return pl;
    }
    
    private ParsedFoldingComponent createRecursiveFoldingComponent(Class <? extends BonaPortable> objClass, String f) {
        ParsedFoldingComponent pfc = new ParsedFoldingComponent();
        int dotIndex = f.indexOf('.');
        if (dotIndex < 0) {
            pfc.setFieldname(f);
            pfc.setComponent(null);
        } else {
            pfc.setFieldname(f.substring(0, dotIndex));
            pfc.setComponent(createRecursiveFoldingComponent(null, f.substring(dotIndex+1)));
        }
        // parse possible indexes, numeric or alphanumeric
        pfc.setIndex(-1);  // default: all nont-existing
        dotIndex = pfc.getFieldname().indexOf('[');
        if (dotIndex >= 0) {
            String indexStr = pfc.getFieldname().substring(dotIndex+1);
            pfc.setFieldname(pfc.getFieldname().substring(0, dotIndex));
            dotIndex = indexStr.indexOf(']');
            if (dotIndex != indexStr.length()-1) {
                LOGGER.error("Unparseable index for field {}: [{}, ignored", pfc.getFieldname(), indexStr);
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
    public void addField(BonaPortable obj) throws E {
        if (obj == null) {
            writeNull(null);
        } else {
            // only write the fields selectively
            // first, optionally create a cached mapping
            Class <? extends BonaPortable> objClass = obj.getClass();
            List<ParsedFoldingComponent> parsedFieldList = parsedMapping.get(objClass);
            if (parsedFieldList == null) {
                parsedFieldList = createParsedFieldList(obj, objClass);
                if (parsedFieldList == null)
                    return;
            }
            // some fieldList has been found if we end up here
            // now perform the output, used the parsed list
            startObject(obj);
            for (ParsedFoldingComponent pfc: parsedFieldList)
                obj.foldedOutput(this, pfc);
            delegateComposer.writeSuperclassSeparator();
        }
    }
}
