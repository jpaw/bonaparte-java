package de.jpaw.bonaparte.converter;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.DataConverter;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;

/**
 * The BigDecimalConverter interface is used to run some preprocessor over objects which are mapped to type BigDecimal.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 */
@Deprecated // use the generic form only
public interface BigDecimalConverter extends DataConverter<BigDecimal,NumericElementaryDataItem> {
}
