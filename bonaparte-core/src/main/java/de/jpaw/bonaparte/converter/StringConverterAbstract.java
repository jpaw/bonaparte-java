package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.util.DataConverterAbstract;

/** Base implementation of StringConverter interface which offers array and nested object support */

@Deprecated // use the generic form only
public abstract class StringConverterAbstract extends DataConverterAbstract<String,AlphanumericElementaryDataItem> implements StringConverter {
}
