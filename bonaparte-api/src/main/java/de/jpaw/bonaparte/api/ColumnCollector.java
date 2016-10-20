package de.jpaw.bonaparte.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.AlphanumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDefinition;
import de.jpaw.bonaparte.pojos.meta.XEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumSetDefinition;
import de.jpaw.bonaparte.pojos.ui.Alignment;
import de.jpaw.bonaparte.pojos.ui.LayoutHint;
import de.jpaw.bonaparte.pojos.ui.UIColumn;
import de.jpaw.bonaparte.pojos.ui.UIColumnConfiguration;
import de.jpaw.bonaparte.pojos.ui.UIDefaults;
import de.jpaw.bonaparte.pojos.ui.UIMeta;
import de.jpaw.bonaparte.util.FieldGetter;
import de.jpaw.bonaparte.util.UtilException;

/** Utility class which helps to guess an initial UI configuration. */
public class ColumnCollector {
    // some tunable constants...
    public static final UIDefaults DEFAULTS = new UIDefaults(5, 32, 24, 80, 120, 10, 12, 200);
    static {
        DEFAULTS.freeze();
    }

    public final List<UIColumn> columns = new ArrayList<UIColumn>();
    private final UIDefaults prefs;
    private final boolean keepObjects;

    public ColumnCollector() {
        prefs = DEFAULTS;
        keepObjects = false;
    }

    public ColumnCollector(UIDefaults preferences) {
        prefs = preferences;
        keepObjects = false;
    }

    public ColumnCollector(UIDefaults preferences, boolean keepObjects) {
        prefs = preferences;
        this.keepObjects = keepObjects;
    }

    private int width(int chars) {
        int calculatedSize = prefs.getWidthOffset() + chars * prefs.getWidthPerCharacter();
        return calculatedSize > prefs.getWidthMax() ? prefs.getWidthMax() : calculatedSize;

    }

    private void addFieldToColumns(String pathname, ClassDefinition cls, FieldDefinition f) {
        // create a preliminary column descriptor
        UIColumn d = new UIColumn();
        d.setFieldName(pathname);
        String type = f.getDataType().toLowerCase();

        if (f instanceof ObjectReference) {
            // probability to descend further...
            ObjectReference o = (ObjectReference)f;
            if (o.getLowerBound() != null) {
                // yes, descend! (discards the descriptor prepared before...)
                if (keepObjects) {
                    d.setWidth(prefs.getWidthObject());
                    d.setAlignment(Alignment.LEFT);  // probably replaced by some description
                    d.setLayoutHint(LayoutHint.OBJECT);
                    columns.add(d);
                }
                addToColumns(pathname + ".", o.getSecondaryLowerBound() != null ? o.getSecondaryLowerBound() : o.getLowerBound());
            } else {
                // regular object
                d.setWidth(prefs.getWidthObject());
                d.setAlignment(Alignment.CENTER);
                d.setLayoutHint(LayoutHint.OBJECT);
                columns.add(d);
            }
            return;
        }

        if (f instanceof BasicNumericElementaryDataItem) {
            // all numerical fields
            BasicNumericElementaryDataItem b = (BasicNumericElementaryDataItem)f;
            d.setWidth(width(b.getTotalDigits() + (b.getIsSigned() ? 1 : 0) + (b.getDecimalDigits() > 0 ? 1 : 0)));
            d.setAlignment(Alignment.RIGHT);
            d.setLayoutHint(LayoutHint.TEXT);
            columns.add(d);
            return;
        }
        if (f instanceof AlphanumericElementaryDataItem) {
            // all alphabetical fields
            AlphanumericElementaryDataItem a = (AlphanumericElementaryDataItem)f;
            d.setWidth(width(a.getLength()));
            d.setAlignment(Alignment.LEFT);
            d.setLayoutHint(LayoutHint.TEXT);
            columns.add(d);
            return;
        }
        if (f instanceof TemporalElementaryDataItem) {
            // all date / time fields: size depends on type
            TemporalElementaryDataItem t = (TemporalElementaryDataItem)f;
            int fractionalSecondsSize = t.getFractionalSeconds() > 0 ? 4 : 0;
            if ("day".equals(type))
                d.setWidth(width(10));
            else if ("time".equals(type))
                d.setWidth(width(8 + fractionalSecondsSize));
            else
                d.setWidth(width(19 + fractionalSecondsSize));
            d.setAlignment(Alignment.CENTER);
            d.setLayoutHint(LayoutHint.TEXT);
            columns.add(d);
            return;
        }
        if (f instanceof MiscElementaryDataItem) {
            // types like char, UUID, boolean
            d.setWidth(width(1));
            d.setAlignment(Alignment.CENTER);
            d.setLayoutHint(LayoutHint.TEXT);
            if ("uuid".equals(type)) {
                d.setWidth(width(36));
            } else if ("boolean".equals(type)) {
                d.setLayoutHint(LayoutHint.CHECKBOX);
            }
            columns.add(d);
            return;
        }

        // enum or enumset...
        String category = f.getDataCategory().name();
        if (category.contains("ENUM")) {
            d.setWidth(category.contains("ENUMSET") ? prefs.getWidthEnumset() : prefs.getWidthEnum());
            d.setAlignment(Alignment.LEFT);
            d.setLayoutHint(LayoutHint.TEXT);
            columns.add(d);
            return;
        }

        // anything else (binary for example) ignore (for images or JSON, context menus / popups should be used)
    }

    private void addToColumnsNoRecursion(String prefix, ClassDefinition cls) {
        for (FieldDefinition f : cls.getFields()) {
            if (f.getMaxCount() != null && f.getMaxCount().intValue() > 0) {
                // make all array elements available
                int num = f.getMaxCount();
                if (num > prefs.getRenderMaxArrayColumns())
                    num = prefs.getRenderMaxArrayColumns(); // but don't overdo!
                for (int i = 0; i < num; ++i)
                    addFieldToColumns(prefix + f.getName() + String.format("[%d]", i), cls, f);
            } else {
                // is a single instance
                addFieldToColumns(prefix + f.getName(), cls, f);
            }
        }
    }

    private void addToColumns(String prefix, ClassDefinition cls) {
        if (cls != null) {
            // first the fields of any superclass...
            if (cls.getParentMeta() != null)
                addToColumns(prefix, cls.getParentMeta());
            // then my own...
            addToColumnsNoRecursion(prefix, cls);
        }
    }

    /** Main external entry. */
    public void addToColumns(ClassDefinition cls) {
        addToColumns("", cls);
    }

    /** Creates the UIMeta object, without the properties yet. */
    public UIMeta createMeta(FieldDefinition meta) {
        UIMeta m = new UIMeta();
        // transfer fields which are in the main class
        m.setIsRequired(meta.getIsRequired());
        m.setDataCategory(meta.getDataCategory().name());
        m.setDataType(meta.getBonaparteType());
        m.setFieldProperties(meta.getProperties());

        switch (meta.getMultiplicity()) {
        case ARRAY:
        case LIST:
        case SET:
            m.setIsList(Boolean.TRUE);
            break;
        case MAP:
            m.setIsMap(Boolean.TRUE);
            break;
        case SCALAR:
            // no entry
            break;

        }

        if (meta instanceof ObjectReference) {
            ObjectReference or = (ObjectReference)meta;
            ClassDefinition bound = or.getSecondaryLowerBound();
            if (bound == null)
                bound = or.getLowerBound();
            if (bound != null) {
                m.setPqon(bound.getName());
                m.setClassProperties(bound.getProperties());
            }
        } else {
            if (meta instanceof BasicNumericElementaryDataItem) {
                BasicNumericElementaryDataItem bn = (BasicNumericElementaryDataItem)meta;
                m.setLength(bn.getTotalDigits());
                m.setIsSigned(bn.getIsSigned());
                m.setDecimalDigits(bn.getDecimalDigits());

            } else if (meta instanceof AlphanumericElementaryDataItem) {
                AlphanumericElementaryDataItem an = (AlphanumericElementaryDataItem)meta;
                m.setLength(an.getLength());
                m.setMinLength(an.getMinLength());
                if (an.getAllowControlCharacters())
                    m.setAllowCtrl(Boolean.TRUE);

            } else if (meta instanceof TemporalElementaryDataItem) {
                TemporalElementaryDataItem tn = (TemporalElementaryDataItem)meta;
                m.setDecimalDigits(tn.getFractionalSeconds());

            } else if (meta instanceof BinaryElementaryDataItem) {
                BinaryElementaryDataItem bn = (BinaryElementaryDataItem)meta;
                m.setLength(bn.getLength());

            } else if (meta instanceof EnumDataItem) {
                EnumDataItem   en           = (EnumDataItem)meta;
                EnumDefinition ed           = en.getBaseEnum();
                m.setPqon(ed.getName());
                m.setEnumInstances(ed.getIds());

            } else if (meta instanceof XEnumDataItem) {
                XEnumDataItem xen           = (XEnumDataItem)meta;
                XEnumDefinition xed         = xen.getBaseXEnum();
                EnumDefinition ed           = xed.getBaseEnum();    // currently we associate the xenum with the base enum.
                m.setPqon2(xed.getName());
                m.setPqon(ed.getName());
                m.setEnumInstances(ed.getIds());

            } else if (meta instanceof AlphanumericEnumSetDataItem) {
                AlphanumericEnumSetDataItem en = (AlphanumericEnumSetDataItem)meta;
                EnumDefinition ed           = en.getBaseEnumset().getBaseEnum();
                m.setPqon(ed.getName());
                m.setEnumInstances(ed.getIds());

            } else if (meta instanceof NumericEnumSetDataItem) {
                NumericEnumSetDataItem en   = (NumericEnumSetDataItem)meta;
                EnumDefinition ed           = en.getBaseEnumset().getBaseEnum();
                m.setPqon(ed.getName());
                m.setEnumInstances(ed.getIds());

            } else if (meta instanceof XEnumSetDataItem) {
                XEnumSetDataItem xs         = (XEnumSetDataItem)meta;
                XEnumSetDefinition xsd      = xs.getBaseXEnumset();
                XEnumDefinition xed         = xsd.getBaseXEnum();
                EnumDefinition ed           = xed.getBaseEnum();    // currently we associate the xenum with the base enum.
                m.setPqon2(xed.getName());
                m.setPqon(ed.getName());
                m.setEnumInstances(ed.getIds());

            } else if (meta instanceof TemporalElementaryDataItem) {
                TemporalElementaryDataItem tn = (TemporalElementaryDataItem)meta;
                m.setDecimalDigits(tn.getFractionalSeconds());
            }
        }

        return m;
    }

    protected void addFieldProperty(String fieldname, String propertyname, String value, Map<String, UIColumnConfiguration> fields) {
        UIColumnConfiguration ui = fields.get(fieldname);
        if (ui != null) {
            UIMeta m = ui.getMeta();
            if (m != null) {   // should be... (we just set it before)
                Map<String, String> fp = m.getFieldProperties();
                if (fp == null) {
                    // first property of this field
                    fp = new HashMap<String, String>();
                    m.setFieldProperties(fp);
                }
                fp.put(propertyname, value);
            }
        }
    }

    /** Given a path name of the form field1[index].field2[index2], with optional array indexes, return a stripped field name field1.field2.
     * Nested brackets are currently NOT supported. */
    public String stripArrayIndexes(String pathname) throws UtilException {
        int i = pathname.indexOf('[');
        if (i < 0) {
            // not a single index contained - skip allocating a StringBuilder and return the original pathname
            return pathname;
        }
        StringBuilder result = new StringBuilder(pathname.length());
        int rest = 0;   // start index of the remaining path
        while (i >= 0) {
            // transfer portion before i
            if (i > rest)
                result.append(pathname.substring(rest, i));
            // find end of skip
            i = pathname.indexOf(']', i+1);
            if (i < 0)
                throw new UtilException(UtilException.NO_CLOSING_BRACKET, pathname);
            rest = i+1;
            i = pathname.indexOf('[', rest);
        }
        // no more brackets found, return the rest
        result.append(pathname.substring(rest));
        return result.toString();
    }

    /** Create meta for a single field. No properties stored. */
    public void createUIMeta(UIColumnConfiguration ui, ClassDefinition cls) throws UtilException {
        String strippedPathname = stripArrayIndexes(ui.getFieldName());  // properties are the same for different indexes of the same field
        FieldDefinition fd = FieldGetter.getFieldDefinitionForPathname(cls, strippedPathname);
        ui.setMeta(createMeta(fd));
    }

    /** Create meta data (now extended to support nested names as well). Returns class level properties.
     * For a method to evaluate the value of a field for a given instance, see FieldGetter.getSingleField in project bonaparte-core */
    public Map<String, String> createUIMetas(List<UIColumnConfiguration> uis, ClassDefinition cls) throws UtilException {
        // create a hash of the field names
        Map<String, UIColumnConfiguration> fields = new HashMap<String, UIColumnConfiguration>(uis.size() * 2);
        Set<String> usedClasses = new HashSet<String>();

        // walk all fields, use the hash to avoid computing similar names multiple times
        for (UIColumnConfiguration ui : uis) {
            String strippedPathname = stripArrayIndexes(ui.getFieldName());  // properties are the same for different indexes of the same field
            UIColumnConfiguration previousUi = fields.get(strippedPathname);
            if (previousUi != null) {
                ui.setMeta(previousUi.getMeta());
            } else {
                // have to compute it
                fields.put(strippedPathname, ui);
                FieldDefinition fd = FieldGetter.getFieldDefinitionForPathname(cls, strippedPathname);
                ui.setMeta(createMeta(fd));
                // if the stripped classname contains at least one dot, remember the class part
                int i = strippedPathname.lastIndexOf('.');
                if (i > 0)
                    usedClasses.add(strippedPathname.substring(0, i));  // remember all classes which host fields
            }
        }

        // now assign the properties at field level

        // TODO: nested fields still TODO - enhance meta information to simplify life...
        Map<String, String> classProperties = new HashMap<String, String>();
        for (Map.Entry<String, String> e : cls.getProperties().entrySet()) {
            String key = e.getKey();
            int i = key.indexOf('.');
            if (i >= 0) {
                // field property
                String fieldname = key.substring(0, i);
                addFieldProperty(fieldname, key.substring(i+1), e.getValue(), fields);
            } else {
                // class property
                classProperties.put(key, e.getValue());
            }
        }

        return classProperties;
    }
}
