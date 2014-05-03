package de.jpaw.bonaparte.core;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/**
 * The StringConverter interface is used to run some preprocessor over objects which are mapped to type String (Ascii, Unicode, Upper, Lower).
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 */
public interface StringConverter extends DataConverter<String,AlphanumericElementaryDataItem> {
}
