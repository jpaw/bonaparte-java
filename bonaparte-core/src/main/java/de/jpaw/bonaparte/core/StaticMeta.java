package de.jpaw.bonaparte.core;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.DataCategory;
import de.jpaw.bonaparte.pojos.meta.Multiplicity;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.Visibility;

public interface StaticMeta {
    static public final int MAX_PQON_LENGTH = 63;     // keep in sync with length in DSL validation class 
    
    public static final AlphanumericElementaryDataItem OBJECT_CLASS = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "objectClass",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.STRING, "Ascii", false, false, false, false, true, true, MAX_PQON_LENGTH, 0, null);
    public static final AlphanumericElementaryDataItem REVISION_META = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "objectRevision",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.STRING, "Ascii", false, false, false, false, false, true, 16, 0, null);
    public static final ObjectReference OUTER_BONAPORTABLE = new ObjectReference(Visibility.PRIVATE, false, "record",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.OBJECT, "BonaPortable", false, false, true, "BonaPortable", null);

    public static final AlphanumericElementaryDataItem MAP_INDEX_META_STRING = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "map$Index",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.STRING, "Unicode", false, true, false, false, false, false, 255, 0, null);
    public static final BasicNumericElementaryDataItem MAP_INDEX_META_INTEGER = new BasicNumericElementaryDataItem(Visibility.PRIVATE, false, "map$Index",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.BASICNUMERIC, "Integer", false, true, true, 9, 0);
    public static final BasicNumericElementaryDataItem MAP_INDEX_META_LONG = new BasicNumericElementaryDataItem(Visibility.PRIVATE, false, "map$Index",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.BASICNUMERIC, "Long", false, true, true, 18, 0);

    public static final AlphanumericElementaryDataItem ENUM_TOKEN = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "enum$Token",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.STRING, "Unicode", false, true, false, false, false, false, 40, 0, null);
    public static final BasicNumericElementaryDataItem INTERNAL_INTEGER = new BasicNumericElementaryDataItem(Visibility.PRIVATE, false, "int$Int",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.BASICNUMERIC, "int", true, false, false, 9, 0);
}
