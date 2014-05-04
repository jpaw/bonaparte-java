package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.core.DataConverter;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

/**
 * The ObjectConverter interface is used to run some preprocessor over all objects.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 */
@Deprecated // use the generic form only
public interface ObjectConverter extends DataConverter<Object,FieldDefinition> {
}
