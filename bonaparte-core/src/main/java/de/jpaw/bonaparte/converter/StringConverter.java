package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.core.DataConverter;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/**
 * The StringConverter interface is used to run some preprocessor over objects which are mapped to type String (Ascii, Unicode, Upper, Lower).
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 */
@Deprecated // use the generic form only
public interface StringConverter extends DataConverter<String,AlphanumericElementaryDataItem> {
}
