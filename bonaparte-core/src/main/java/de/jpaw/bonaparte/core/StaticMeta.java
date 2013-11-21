package de.jpaw.bonaparte.core;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.DataCategory;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.Multiplicity;
import de.jpaw.bonaparte.pojos.meta.Visibility;

public interface StaticMeta {
    static public final int MAX_PQON_LENGTH = 63;     // keep in sync with length in DSL validation class 
    
    public static final AlphanumericElementaryDataItem OBJECT_CLASS = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "objectClass",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.STRING, "Ascii", false, false, false, false, true, MAX_PQON_LENGTH, 0, null);
    public static final AlphanumericElementaryDataItem REVISION_META = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "objectRevision",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.STRING, "Ascii", false, false, false, false, true, 16, 0, null);
    public static final MiscElementaryDataItem OUTER_BONAPORTABLE = new MiscElementaryDataItem(Visibility.PRIVATE, false, "record",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.MISC, "Object", false);
    public static final AlphanumericElementaryDataItem MAP_INDEX_META = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "map$Index",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.STRING, "Unicode", false, true, false, false, false, 255, 0, null);
    public static final AlphanumericElementaryDataItem ENUM_TOKEN = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "enum$Token",
            Multiplicity.SCALAR, 0, 0, 0, DataCategory.STRING, "Unicode", false, true, false, false, false, 40, 0, null);
}
