package de.jpaw.bonaparte.core;

import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

/** DTO class for the extended List composer */
public class DataAndMeta {
    public final FieldDefinition meta;
    public final Object data;
    
    public DataAndMeta(FieldDefinition meta, Object data) {
        this.meta = meta;
        this.data = data;
    }
}
