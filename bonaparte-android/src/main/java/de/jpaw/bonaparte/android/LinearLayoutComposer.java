package de.jpaw.bonaparte.android;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import de.jpaw.bonaparte.core.AbstractMessageComposer;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;

/** Composer which is designed to work as a delegate for a foldingComposer.
 * Therefore object output itself is not supported. */

abstract public class LinearLayoutComposer extends AbstractMessageComposer<RuntimeException> implements AndroidViewComposer, View.OnClickListener {
    static private final Logger LOG = LoggerFactory.getLogger(LinearLayoutComposer.class);
    protected int rownum = -1;
    protected int column = 0;
    protected AndroidObjectClickListener onClickListener;
    protected Map<Integer,BonaCustom> buttonPayload;  // this stores the object for an ID
    protected boolean expandObjects = true;             // set to false if we sit behind a folding composer

    static protected final int ROW_FACTOR = 10000;  // for the ID calculation, take the column + ROW_FACTOR * row
    protected boolean binaryAsBitmap = false;
    protected static final String [] BIGDECIMAL_FORMATS = {
        "#0",
        "#0.#",
        "#0.##",
        "#0.###",
        "#0.####",
        "#0.#####",
        "#0.######",
        "#0.#######",
        "#0.########",
        "#0.#########",
        "#0.##########",
        "#0.###########",
        "#0.############",
        "#0.#############",
        "#0.##############",
        "#0.###############",
        "#0.################",
        "#0.#################",
        "#0.##################"
    };

    // methods required in superclass
    abstract CheckBox needCheckBox(FieldDefinition di);
    abstract TextView needTextView(FieldDefinition di);
    abstract ImageView needImageView(FieldDefinition di);
    abstract Button needButton(FieldDefinition di);
    abstract View needAny(FieldDefinition di);

    // creates a new composer, requires a subsequent newView() to set the rowWidget
    public LinearLayoutComposer() {
    }

    public void setExpandObjects(boolean doIt) {
        expandObjects = doIt;
    }
    public void setBinaryAsBitmap(boolean doIt) {
        binaryAsBitmap = doIt;
    }
    public int getId() {
        return column + ROW_FACTOR * rownum;
    }
    public int getIdFromRowAndColumn(int row, int column) {
        return column + ROW_FACTOR * row;
    }
    public void setOnClickListener(AndroidObjectClickListener onClickListener) {
        this.onClickListener = onClickListener;
        if (onClickListener == null) {
            buttonPayload = null;
        } else {
            buttonPayload = new HashMap<Integer,BonaCustom>();
        }
    }
    // called if a button (to show an object) has been clicked.
    @Override
    public void onClick(View v) {
        if (onClickListener != null && buttonPayload != null) {
            int id = v.getId();
            BonaCustom obj = buttonPayload.get(id);
            onClickListener.onClick(v, obj, id / ROW_FACTOR, id % ROW_FACTOR);
        }
    }

    /**************************************************************************************************
     * Serialization goes here
     **************************************************************************************************/


    @Override
    public void writeNull(FieldDefinition di) {
        needAny(di);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) {
    }

    @Override
    public void startTransmission() {
        rownum = -1;
    }
    @Override
    public void terminateTransmission() {
    }

    @Override
    public void terminateRecord() {
    }

    @Override
    public void writeSuperclassSeparator() {
    }

    @Override
    public void startRecord() {
        ++rownum;
    }

    @Override
    public void writeRecord(BonaCustom o) {
        startRecord();
        addField(StaticMeta.OUTER_BONAPORTABLE, o);
        terminateRecord();
    }
    private void newTextView(FieldDefinition di, String s) {
        TextView tv = needTextView(di);
        tv.setId(getId());
        tv.setText(s != null ? s : "");
    }

    // field type specific output functions

    // character
    @Override
    public void addField(MiscElementaryDataItem di, char c) {
        newTextView(di, String.valueOf(c));
    }
    // ascii only (unicode uses different method)
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
        newTextView(di, s);
    }

    private void outputBigDecimal(BasicNumericElementaryDataItem di, BigDecimal n) {
        int digs = di.getDecimalDigits();
        DecimalFormat df = new DecimalFormat(BIGDECIMAL_FORMATS[digs]);
        df.setMaximumFractionDigits(digs);
        df.setMinimumFractionDigits(digs);
        df.setGroupingUsed(true);
        newTextView(di, df.format(n));
    }

    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
        if (n != null) {
            outputBigDecimal(di, n);
        } else {
            writeNull(di);
        }
    }

    // byte
    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) {
        if (di.getDecimalDigits() > 0)
            outputBigDecimal(di, BigDecimal.valueOf(n, di.getDecimalDigits()));
        else
            newTextView(di, Byte.toString(n));
    }
    // short
    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) {
        if (di.getDecimalDigits() > 0)
            outputBigDecimal(di, BigDecimal.valueOf(n, di.getDecimalDigits()));
        else
            newTextView(di, Short.toString(n));
    }
    // integer
    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) {
        if (di.getDecimalDigits() > 0)
            outputBigDecimal(di, BigDecimal.valueOf(n, di.getDecimalDigits()));
        else
            newTextView(di, Integer.toString(n));
    }

    // long
    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) {
        if (di.getDecimalDigits() > 0)
            outputBigDecimal(di, BigDecimal.valueOf(n, di.getDecimalDigits()));
        else
            newTextView(di, Long.toString(n));
    }

    // int(n)
    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) {
        if (n != null) {
            newTextView(di, n.toString());
        } else {
            writeNull(di);
        }
    }

    // boolean
    @Override
    public void addField(MiscElementaryDataItem di, boolean b) {
        needCheckBox(di).setChecked(b);
    }

    // float
    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) {
        newTextView(di, Float.toString(f));
    }

    // double
    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) {
        newTextView(di, Double.toString(d));
    }

    // UUID
    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
        if (n != null) {
            newTextView(di, n.toString());
        } else {
            writeNull(di);
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
        if (b != null) {
            if (binaryAsBitmap) {
                byte [] rawData = b.getBytes();
                needImageView(di).setImageBitmap(BitmapFactory.decodeByteArray(rawData, 0, rawData.length));
            } else {
                newTextView(di, "(Binary)");
            }
        } else {
            writeNull(di);
        }
    }

    // raw
    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
        if (b != null) {
            if (binaryAsBitmap) {
                needImageView(di).setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
            } else {
                newTextView(di, "(Binary)");
            }
        } else {
            writeNull(di);
        }
    }

    // converters for DAY und TIMESTAMP
    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
        if (t != null) {
            newTextView(di, t.toString());
        } else {
            writeNull(di);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
        if (t != null) {
            newTextView(di, t.toString());
        } else {
            writeNull(di);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) {
        if (t != null) {
            newTextView(di, t.toString());
        } else {
            writeNull(di);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) {
        if (t != null) {
            newTextView(di, t.toString());
        } else {
            writeNull(di);
        }
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) {
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) {
    }

    @Override
    public void terminateArray() {
    }

    @Override
    public void terminateMap() {
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom obj) {
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) {
    }

    /** Adding objects will lead to column misalignment if the objects itself are null. */
    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        LOG.info("adding OBJECT in row {}, expand = {}", rownum, expandObjects);
        if (expandObjects) {
            if (obj != null) {
                obj.serializeSub(this);
            }
        } else {
            // this is a single column. Display as a button, with onClickListener to set a callback
            Button b = needButton(di);
            if (buttonPayload != null)
                buttonPayload.put(b.getId(), obj);
            LOG.info("   button id = {}, object is {}", b.getId(), obj == null ? "(null)" : obj.ret$PQON());
            if (obj == null) {
                b.setText("null");
                b.setEnabled(false);
            } else {
                b.setText("...");
                b.setEnabled(true);
            }
        }
    }

    // enum with numeric expansion: delegate to Null/Int
    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) {
        if (n == null) {
            writeNull(ord);
        } else {
            addField(ord, n.ordinal());
        }
    }

    // enum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) {
        if (n == null) {
            writeNull(token);
        } else {
            addField(token, n.getToken());
        }
    }

    // xenum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) {
        if (n == null) {
            writeNull(token);
        } else {
            addField(token, n.getToken());
        }
    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) {
        if (obj != null) {
            newTextView(di, obj.toString());
        } else {
            writeNull(di);
        }
        return true;       // for the UI display, use the string representation by default
    }
}
