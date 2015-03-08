package de.jpaw.bonaparte.api;

import java.util.ArrayList;
import java.util.List;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.ui.Alignment;
import de.jpaw.bonaparte.pojos.ui.LayoutHint;
import de.jpaw.bonaparte.pojos.ui.UIColumn;
import de.jpaw.bonaparte.pojos.ui.UIDefaults;

/** Utility class which helps to guess an initial UI configuration. */
public class ColumnCollector {
    // some tunable constants...
    public static final UIDefaults DEFAULTS = new UIDefaults(5, 32, 24, 80, 120, 10, 12, 200);
    static {
        DEFAULTS.freeze();
    }

    public final List<UIColumn> columns = new ArrayList<UIColumn>();
    private final UIDefaults prefs;

    public ColumnCollector() {
        prefs = DEFAULTS;
    }

    public ColumnCollector(UIDefaults preferences) {
        prefs = preferences;
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

        // anything else (binary for example) ignore
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
}
