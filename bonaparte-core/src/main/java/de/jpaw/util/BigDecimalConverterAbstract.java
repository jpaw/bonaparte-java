package de.jpaw.util;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.BigDecimalConverter;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;

/** Base implementation of BigDecimalConverter interface which offers array and nested object support */

public abstract class BigDecimalConverterAbstract extends DataConverterAbstract<BigDecimal,NumericElementaryDataItem> implements BigDecimalConverter {
}
