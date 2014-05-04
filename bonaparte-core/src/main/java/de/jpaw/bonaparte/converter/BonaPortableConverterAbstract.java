package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.util.DataConverterAbstract;

/** Base implementation of BonaPortableConverter interface which offers array and nested object support */

@Deprecated // use the generic form only
public abstract class BonaPortableConverterAbstract extends DataConverterAbstract<BonaPortable,ObjectReference> implements BonaPortableConverter {
}
