/*
 * Copyright 2012 Michael Bischoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.util.ByteArray;
/**
 * The CSVComposer class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implements the serialization for the bonaparte format using a character Appendable, for CSV output
 */

public class CSVComposer extends AppendableComposer {

    protected boolean recordStart = true;
    //protected boolean shouldWarnWhenUsingFloat;
    protected final CSVConfiguration cfg;
    // derived data
    protected final String stringQuote;       // quote character for strings, as a string
    //protected final boolean usesDefaultDecimalPoint;   // just for speedup, to avoid frequent String equals()
    protected final DateTimeFormatter dayFormat;            // day without time (Joda)
    protected final DateTimeFormatter timeFormat;           // time on second precision (Joda)
    protected final DateTimeFormatter time3Format;          // time on millisecond precision (Joda)
    protected final DateTimeFormatter timestampFormat;      // day and time on second precision (Joda)
    protected final DateTimeFormatter timestamp3Format;     // day and time on millisecond precision (Joda)
    protected Format numberFormat;              // locale's default format for formatting float and double, covers decimal point and sign
    protected final NumberFormat bigDecimalFormat;          // locale's default format for formatting BigDecimal, covers decimal point and sign


    public CSVComposer(Appendable work, CSVConfiguration cfg) {
        super(work, ObjectReuseStrategy.NONE);  // CSV does not know about object backreferences...
        this.cfg = cfg;
        this.stringQuote = (cfg.quote != null) ? String.valueOf(cfg.quote) : "";  // use this for cases where a String is required
        //this.usesDefaultDecimalPoint = cfg.decimalPoint.equals(".");
        //this.shouldWarnWhenUsingFloat = cfg.decimalPoint.length() == 0;     // removing decimal points from float or double is a bad idea, because no scale is defined
        this.dayFormat = cfg.determineDayFormatter().withLocale(cfg.locale).withZoneUTC();
        this.timeFormat = cfg.determineTimeFormatter().withLocale(cfg.locale).withZoneUTC();
        this.time3Format = cfg.determineTime3Formatter().withLocale(cfg.locale).withZoneUTC();
        this.timestampFormat = cfg.determineTimestampFormatter().withLocale(cfg.locale).withZoneUTC();
        this.timestamp3Format = cfg.determineTimestamp3Formatter().withLocale(cfg.locale).withZoneUTC();
        NumberFormat myNumberFormat = NumberFormat.getInstance(cfg.locale);
        myNumberFormat.setGroupingUsed(false);                           // this is for interfaces, don't do pretty-printing
        this.numberFormat = myNumberFormat;
        this.bigDecimalFormat = cfg.removePoint4BD ? null : (NumberFormat)myNumberFormat.clone();    // make a copy for BigDecimal, where we set fractional digits as required
        //this.decimalFormat = this.numberFormat instanceof DecimalFormat ? (DecimalFormat)numberFormat : null;
    }

    protected void writeSeparator() throws IOException {   // nothing to do in the standard bonaparte format
        if (recordStart)
            recordStart = false;
        else
            addRawData(cfg.separator);
    }


    @Override
    protected void terminateField() {
    }

    @Override
    public void writeNull(FieldDefinition di) throws IOException {
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws IOException {
    }

    @Override
    public void startTransmission() {
    }

    @Override
    public void terminateTransmission() {
    }

    @Override
    public void writeSuperclassSeparator() {
    }

    @Override
    public void startRecord() {
        recordStart = true;
    }

    private void addCharSub(char c) throws IOException {
        addRawData(cfg.quote != null && c == cfg.quote ? stringQuote : c < 0x20 ? cfg.ctrlReplacement : String.valueOf(c));
    }

    // field type specific output functions

    // character
    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
        writeSeparator();
        addCharSub(c);
    }
    // ascii only (unicode uses different method)
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        writeSeparator();
        if (s != null) {
            addRawData(stringQuote);
            for (int i = 0; i < s.length(); ++i) {
                addCharSub(s.charAt(i));
            }
            addRawData(stringQuote);
        }
    }

    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        writeSeparator();
        if (n != null) {
            if (cfg.removePoint4BD) {
                // use standard BigDecimal formatter, and remove the "." from the output
                addRawData(n.setScale(di.getDecimalDigits()).toPlainString().replace(".", ""));
            } else {
                // use standard locale formatter to get the localized . or ,
                bigDecimalFormat.setMaximumFractionDigits(n.scale());
                bigDecimalFormat.setMinimumFractionDigits(n.scale());
                addRawData(bigDecimalFormat.format(n));
            }
        }
    }

    // byte
    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        writeSeparator();
        super.addField(di, n);
    }
    // short
    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        writeSeparator();
        super.addField(di, n);
    }
    // integer
    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        writeSeparator();
        super.addField(di, n);
    }

    // int(n)
    @Override
    public void addField(BasicNumericElementaryDataItem di, Integer n) throws IOException {
        writeSeparator();
        super.addField(di, n);
    }

    // long
    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        writeSeparator();
        super.addField(di, n);
    }

    // boolean
    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
        writeSeparator();
        super.addRawData(b ? cfg.booleanTrue : cfg.booleanFalse);
    }

    // float
    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        writeSeparator();
        addRawData(numberFormat.format(f));            // format using the locale's approach
        /*
        String defaultFormat = Float.toString(f);
        addRawData(usesDefaultDecimalPoint ? defaultFormat : defaultFormat.replace(".", cfg.decimalPoint));
        if (shouldWarnWhenUsingFloat) {
            shouldWarnWhenUsingFloat = false;  // only warn once per record
            LOGGER.warn("Using float or double and removal of decimal point may result in undefined output");
        } */
    }

    // double
    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        writeSeparator();
        addRawData(numberFormat.format(d));            // format using the locale's approach
        /*
        String defaultFormat = Double.toString(d);
        addRawData(usesDefaultDecimalPoint ? defaultFormat : defaultFormat.replace(".", cfg.decimalPoint));
        if (shouldWarnWhenUsingFloat) {
            shouldWarnWhenUsingFloat = false;  // only warn once per record
            LOGGER.warn("Using float or double and removal of decimal point may result in undefined output");
        } */
    }

    // UUID
    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        writeSeparator();
        if (n != null) {
            addRawData(stringQuote);
            super.addField(di, n);
            addRawData(stringQuote);
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws IOException {
        writeSeparator();
        if (b != null) {
            addRawData(stringQuote);
            super.addField(di, b);
            addRawData(stringQuote);
        } else {
            writeNull(di);
        }
    }

    // raw
    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
        writeSeparator();
        if (b != null) {
            addRawData(stringQuote);
            super.addField(di, b);
            addRawData(stringQuote);
        } else {
            writeNull(di);
        }
    }

    // converters for DAY und TIMESTAMP
    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws IOException {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            addRawData(dayFormat.print(t));
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        } else {
            writeNull(di);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) throws IOException {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            if (di.getFractionalSeconds() <= 0)
                addRawData(timeFormat.print(t));   // second precision
            else
                addRawData(time3Format.print(t));  // millisecond precision
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        } else {
            writeNull(di);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            if (di.getFractionalSeconds() <= 0)
                addRawData(timestampFormat.print(t));   // second precision
            else
                addRawData(timestamp3Format.print(t));  // millisecond precision
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        } else {
            writeNull(di);
        }
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws IOException {
        if (cfg.mapStart != null && cfg.mapStart.length() > 0) {
            super.addRawData(cfg.mapStart);
            recordStart = true;
        }
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws IOException {
        if (cfg.arrayStart != null && cfg.arrayStart.length() > 0) {
            super.addRawData(cfg.arrayStart);
            recordStart = true;
        }
    }

    @Override
    public void terminateArray() throws IOException {
        if (cfg.arrayEnd != null && cfg.arrayEnd.length() > 0) {
            super.addRawData(cfg.arrayEnd);
            recordStart = true;
        }
    }

    @Override
    public void terminateMap() throws IOException {
        if (cfg.mapEnd != null && cfg.mapEnd.length() > 0) {
            super.addRawData(cfg.mapEnd);
            recordStart = true;
        }
    }

    @Override
    public void startObject(ObjectReference di, BonaPortable obj) throws IOException {
        if (cfg.objectStart != null && cfg.objectStart.length() > 0) {
            super.addRawData(cfg.objectStart);
            recordStart = true;
        }
    }
  
    @Override
    public void terminateObject(ObjectReference di, BonaPortable obj) throws IOException {
        if (cfg.objectEnd != null && cfg.objectEnd.length() > 0) {
            super.addRawData(cfg.objectEnd);
            recordStart = true;
        }
    }
  
    @Override
    public void addField(ObjectReference di, BonaPortable obj) throws IOException {
        if (obj != null) {
            startObject(di, obj);
            // do all fields
            obj.serializeSub(this);
            terminateObject(di, obj);
        }
    }
}
