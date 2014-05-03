package de.jpaw.util;

import de.jpaw.bonaparte.core.StringConverter;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/** Base implementation of StringConverter interface which offers array and nested object support */

public abstract class StringConverterAbstract extends DataConverterAbstract<String,AlphanumericElementaryDataItem> implements StringConverter {
}
