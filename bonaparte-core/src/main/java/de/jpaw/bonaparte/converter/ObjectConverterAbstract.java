package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

/** Base implementation of ObjectConverter interface which offers array and nested object support */

@Deprecated // use the generic form only
public abstract class ObjectConverterAbstract extends DataConverterAbstract<Object,FieldDefinition> implements ObjectConverter {
}
