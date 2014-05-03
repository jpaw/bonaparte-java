package de.jpaw.bonaparte.core;

import java.math.BigDecimal;

import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;

/**
 * The BigDecimalConverter interface is used to run some preprocessor over objects which are mapped to type BigDecimal.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 */
public interface BigDecimalConverter extends DataConverter<BigDecimal,NumericElementaryDataItem> {
}
