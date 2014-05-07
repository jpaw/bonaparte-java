package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.DataConverter;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

/**
 * The BonaPortableConverter interface is used to run some preprocessor over all BonaPortables (sub objects).
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 */
@Deprecated // use the generic form only
public interface BonaPortableConverter extends DataConverter<BonaPortable,ObjectReference> {
}
