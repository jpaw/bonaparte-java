package de.jpaw.bonaparte.core;

import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

/** DTO class for the extended List composer */
public class DataAndMeta<JAVATYPE,METATYPE extends FieldDefinition> {
    public final METATYPE meta;
    public final JAVATYPE data;
    
    public DataAndMeta(METATYPE meta, JAVATYPE data) {
        this.meta = meta;
        this.data = data;
    }
}
