package de.jpaw.bonaparte.converter;

import java.math.BigDecimal;

import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.util.DataConverterAbstract;

/** Base implementation of BigDecimalConverter interface which offers array and nested object support */

@Deprecated // use the generic form only
public abstract class BigDecimalConverterAbstract extends DataConverterAbstract<BigDecimal,NumericElementaryDataItem> implements BigDecimalConverter {
}
