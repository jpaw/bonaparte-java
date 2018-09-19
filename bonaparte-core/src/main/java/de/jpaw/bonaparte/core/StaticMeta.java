package de.jpaw.bonaparte.core;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.DataCategory;
import de.jpaw.bonaparte.pojos.meta.IndexType;
import de.jpaw.bonaparte.pojos.meta.Multiplicity;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.Visibility;

public interface StaticMeta {
    static public final int MAX_PQON_LENGTH = 255;     // keep in sync with length in DSL validation class

    public static final AlphanumericElementaryDataItem OBJECT_CLASS = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "objectClass",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.STRING, "ascii", "String", false, false, null, false, false, true, true, MAX_PQON_LENGTH, 0, null);
    public static final AlphanumericElementaryDataItem REVISION_META = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "objectRevision",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.STRING, "ascii", "String", false, false, null, false, false, false, true, 16, 0, null);
    public static final ObjectReference OUTER_BONAPORTABLE = new ObjectReference(Visibility.PRIVATE, false, "record",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.OBJECT, "object", "BonaPortable", false, false, null, true, "BonaPortable", null, null, null);
    public static final ObjectReference INNER_BONAPORTABLE = new ObjectReference(Visibility.PRIVATE, false, "field",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.OBJECT, "object", "BonaPortable", false, false, null, true, "BonaPortable", null, null, null);
    // CSV special: no subclasses allowed (for parsing)
    public static final ObjectReference OUTER_BONAPORTABLE_FOR_CSV = new ObjectReference(Visibility.PRIVATE, false, "record",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.OBJECT, "object", "BonaPortable", false, false, null, false, "BonaPortable", null, null, null);
    // JSON special: keep the name of the outer record secret! PQON is output only when allowSubclasses is true
    public static final ObjectReference OUTER_BONAPORTABLE_FOR_JSON = new ObjectReference(Visibility.PRIVATE, false, "",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.OBJECT, "json", "Map", false, false, null, true, "Map", null, null, null);
    public static final ObjectReference OUTER_BONAPORTABLE_FOR_ARRAY = new ObjectReference(Visibility.PRIVATE, false, "",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.OBJECT, "array", "List", false, false, null, false, "List", null, null, null);
    public static final ObjectReference OUTER_BONAPORTABLE_FOR_ELEMENT = new ObjectReference(Visibility.PRIVATE, false, "",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.OBJECT, "element", "Object", false, false, null, false, "Object", null, null, null);

    public static final AlphanumericElementaryDataItem MAP_INDEX_META_STRING = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "map$Index",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.STRING, "unicode", "String", false, true, null, false, false, false, false, 255, 0, null);
    public static final BasicNumericElementaryDataItem MAP_INDEX_META_INTEGER = new BasicNumericElementaryDataItem(Visibility.PRIVATE, false, "map$Index",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.BASICNUMERIC, "integer", "Integer", false, true, null, true, 9, 0, false);
    public static final BasicNumericElementaryDataItem MAP_INDEX_META_LONG = new BasicNumericElementaryDataItem(Visibility.PRIVATE, false, "map$Index",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.BASICNUMERIC, "long", "Long", false, true, null, true, 18, 0, false);

    public static final AlphanumericElementaryDataItem ENUM_TOKEN = new AlphanumericElementaryDataItem(Visibility.PRIVATE, false, "enum$Token",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.STRING, "enum", "Unicode", false, true, null, false, false, false, false, 40, 0, null);
    public static final BasicNumericElementaryDataItem INTERNAL_INTEGER = new BasicNumericElementaryDataItem(Visibility.PRIVATE, false, "int$Int",
            Multiplicity.SCALAR, IndexType.NONE, 0, 0, DataCategory.BASICNUMERIC, "int", "int", true, false, null, false, 9, 0, false); // used for map / set size
}
