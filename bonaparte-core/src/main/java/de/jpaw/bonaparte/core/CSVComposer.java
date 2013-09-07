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
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVComposer.class);

    protected boolean recordStart = true;
    //protected boolean shouldWarnWhenUsingFloat;
    protected final CSVConfiguration cfg;
    // derived data
    protected final String stringQuote;       // quote character for strings, as a string
    //protected final boolean usesDefaultDecimalPoint;   // just for speedup, to avoid frequent String equals()
    protected final DateTimeFormatter dayFormat;            // day without time (Joda)
    protected final DateTimeFormatter timestampFormat;      // day and time on second precision (Joda)
    protected final DateTimeFormatter timestamp3Format;     // day and time on millisecond precision (Joda)
    protected final DateFormat calendarFormat;              // Java's mutable Calendar. Use with caution (or better, don't use at all)
    protected Format numberFormat;              // locale's default format for formatting float and double, covers decimal point and sign
    protected final NumberFormat bigDecimalFormat;          // locale's default format for formatting BigDecimal, covers decimal point and sign

    private DateFormat determineCalendarFormat(CSVConfiguration cfg) {
        try {
            return cfg.customCalendarFormat == null ? DateFormat.getDateInstance(DateFormat.MEDIUM, cfg.locale) : new SimpleDateFormat(cfg.customCalendarFormat, cfg.locale);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + cfg.customCalendarFormat, e);
            return new SimpleDateFormat(CSVConfiguration.DEFAULT_CALENDAR_FORMAT);// use default locale now, format must be corrected anyway
        }
    }

    private DateTimeFormatter determineDayFormatter(CSVConfiguration cfg) {
        try {
            return cfg.customTimestampFormat == null
                    ? DateTimeFormat.forStyle(cfg.dateStyle.getToken() + "-")
                    : DateTimeFormat.forPattern(cfg.customDayFormat);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + cfg.customDayFormat, e);
            return DateTimeFormat.forPattern(CSVConfiguration.DEFAULT_DAY_FORMAT);
        }
    }

    private DateTimeFormatter determineTimestampFormatter(CSVConfiguration cfg) {
        try {
            return cfg.customTimestampFormat == null
                    ? DateTimeFormat.forStyle(cfg.dateStyle.getToken() + cfg.timeStyle.getToken())
                    : DateTimeFormat.forPattern(cfg.customTimestampFormat);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + cfg.customTimestampFormat, e);
            return DateTimeFormat.forPattern(CSVConfiguration.DEFAULT_TIMESTAMP_FORMAT);
        }
    }

    private DateTimeFormatter determineTimestamp3Formatter(CSVConfiguration cfg) {
        try {
            return cfg.customTimestampWithMsFormat == null
                    ? DateTimeFormat.forStyle(cfg.dateStyle.getToken() + cfg.timeStyle.getToken())
                    : DateTimeFormat.forPattern(cfg.customTimestampWithMsFormat);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + cfg.customTimestampWithMsFormat, e);
            return DateTimeFormat.forPattern(CSVConfiguration.DEFAULT_TS_WITH_MS_FORMAT);
        }
    }

    public CSVComposer(Appendable work, CSVConfiguration cfg) {
        super(work, ObjectReuseStrategy.NONE);  // CSV does not know about object backreferences...
        this.cfg = cfg;
        this.stringQuote = (cfg.quote != null) ? String.valueOf(cfg.quote) : "";  // use this for cases where a String is required
        //this.usesDefaultDecimalPoint = cfg.decimalPoint.equals(".");
        //this.shouldWarnWhenUsingFloat = cfg.decimalPoint.length() == 0;     // removing decimal points from float or double is a bad idea, because no scale is defined
        this.dayFormat = determineDayFormatter(cfg).withLocale(cfg.locale).withZoneUTC();
        this.timestampFormat = determineTimestampFormatter(cfg).withLocale(cfg.locale).withZoneUTC();
        this.timestamp3Format = determineTimestamp3Formatter(cfg).withLocale(cfg.locale).withZoneUTC();
        this.calendarFormat = determineCalendarFormat(cfg);
        this.calendarFormat.setCalendar(Calendar.getInstance(cfg.locale));
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
    public void writeNull() {
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
    @Override
    public void addUnicodeString(String s, int length, boolean allowCtrls) throws IOException {
        writeSeparator();
        if (s != null) {
            addRawData(stringQuote);
            for (int i = 0; i < s.length(); ++i) {
                addCharSub(s.charAt(i));
            }
            addRawData(stringQuote);
        }
    }

    // character
    @Override
    public void addField(char c) throws IOException {
        writeSeparator();
        addCharSub(c);
    }
    // ascii only (unicode uses different method)
    @Override
    public void addField(String s, int length) throws IOException {
        addUnicodeString(s, length, true);
    }

    // decimal
    @Override
    public void addField(BigDecimal n, int length, int decimals,
            boolean isSigned) throws IOException {
        writeSeparator();
        if (n != null) {
            if (cfg.removePoint4BD) {
                // use standard BigDecimal formatter, and remove the "." from the output
                addRawData(n.setScale(decimals).toPlainString().replace(".", ""));
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
    public void addField(byte n) throws IOException {
        writeSeparator();
        super.addField(n);
    }
    // short
    @Override
    public void addField(short n) throws IOException {
        writeSeparator();
        super.addField(n);
    }
    // integer
    @Override
    public void addField(int n) throws IOException {
        writeSeparator();
        super.addField(n);
    }

    // int(n)
    @Override
    public void addField(Integer n, int length, boolean isSigned) throws IOException {
        writeSeparator();
        super.addField(n, length, isSigned);
    }

    // long
    @Override
    public void addField(long n) throws IOException {
        writeSeparator();
        super.addField(n);
    }

    // boolean
    @Override
    public void addField(boolean b) throws IOException {
        writeSeparator();
        super.addRawData(b ? cfg.booleanTrue : cfg.booleanFalse);
    }

    // float
    @Override
    public void addField(float f) throws IOException {
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
    public void addField(double d) throws IOException {
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
    public void addField(UUID n) throws IOException {
        writeSeparator();
        if (n != null) {
            addRawData(stringQuote);
            super.addField(n);
            addRawData(stringQuote);
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(ByteArray b, int length) throws IOException {
        writeSeparator();
        if (b != null) {
            addRawData(stringQuote);
            super.addField(b, length);
            addRawData(stringQuote);
        }
    }

    // raw
    @Override
    public void addField(byte[] b, int length) throws IOException {
        writeSeparator();
        if (b != null) {
            addRawData(stringQuote);
            super.addField(b, length);
            addRawData(stringQuote);
        }
    }

    // converters for DAY und TIMESTAMP
    @Override
    public void addField(Calendar t, boolean hhmmss, int length) throws IOException {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            addRawData(calendarFormat.format(t.getTime()));
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        }
    }
    @Override
    public void addField(LocalDate t) throws IOException {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            addRawData(dayFormat.print(t));
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        }
    }

    @Override
    public void addField(LocalDateTime t, boolean hhmmss, int length) throws IOException {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            if (length == 0)
                addRawData(timestampFormat.print(t));   // second precision
            else
                addRawData(timestamp3Format.print(t));  // millisecond precision
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        }
    }

    @Override
    public void startMap(int currentMembers, int indexID) throws IOException {
        if (cfg.mapStart != null && cfg.mapStart.length() > 0) {
            super.addRawData(cfg.mapStart);
            recordStart = true;
        }
    }

    @Override
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) throws IOException {
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
    public void startObject(BonaPortable obj) throws IOException {
        if (cfg.objectStart != null && cfg.objectStart.length() > 0) {
            super.addRawData(cfg.objectStart);
            recordStart = true;
        }
    }
  
    @Override
    public void addField(BonaPortable obj) throws IOException {
        if (obj != null) {
            startObject(obj);
            // do all fields (now includes terminator)
            obj.serializeSub(this);
            if (cfg.objectEnd != null && cfg.objectEnd.length() > 0) {
                super.addRawData(cfg.objectEnd);
                recordStart = true;
            }
        }
    }
}
