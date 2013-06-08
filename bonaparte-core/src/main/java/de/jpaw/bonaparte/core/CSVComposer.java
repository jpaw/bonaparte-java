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

import java.math.BigDecimal;
import java.text.DateFormat;
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
 *          Implements the serialization for the bonaparte format using StringBuilder, for CSV output
 */

public class CSVComposer extends StringBuilderComposer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVComposer.class);
    
    protected boolean recordStart = true;
    protected boolean shouldWarnWhenUsingFloat;
    protected final CSVConfiguration cfg;
    // derived data
    protected final String stringQuote;       // quote character for strings, as a string
    protected final boolean usesDefaultDecimalPoint;   // just for speedup, to avoid frequent String equals()
    protected final DateTimeFormatter dateTimeFormat;
    protected final DateTimeFormatter dateFormat;
    protected final DateFormat calendarFormat; 

    public CSVComposer(StringBuilder work, CSVConfiguration config) {
        super(work);
        this.cfg = config;
        this.stringQuote = (cfg.quote != null) ? String.valueOf(cfg.quote) : "";  // use this for cases where a String is required
        this.usesDefaultDecimalPoint = cfg.decimalPoint.equals(".");
        this.shouldWarnWhenUsingFloat = cfg.decimalPoint.length() == 0;  // removing decimal points from float or double is a bad idea, because no scale is defined
        this.dateTimeFormat = DateTimeFormat.forStyle(cfg.dateStyle.getToken() + cfg.timeStyle.getToken()).withLocale(cfg.locale).withZoneUTC();
        this.dateFormat = DateTimeFormat.forStyle(cfg.dateStyle.getToken() + "-").withZoneUTC().withLocale(cfg.locale);
        this.calendarFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, cfg.locale);
    }

    protected void writeSeparator() {   // nothing to do in the standard bonaparte format
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

    private void addCharSub(char c) {
        addRawData(c == cfg.quote ? stringQuote : c < 0x20 ? cfg.ctrlReplacement : String.valueOf(c));
    }
    
    // field type specific output functions
    @Override
    public void addUnicodeString(String s, int length, boolean allowCtrls) {
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
    public void addField(char c) {
        writeSeparator();
        addCharSub(c);
    }
    // ascii only (unicode uses different method)
    @Override
    public void addField(String s, int length) {
        addUnicodeString(s, length, true);
    }

    // decimal
    @Override
    public void addField(BigDecimal n, int length, int decimals,
            boolean isSigned) {
        writeSeparator();
        if (n != null) {
            String defaultFormat = n.toPlainString();
            addRawData(usesDefaultDecimalPoint ? defaultFormat : defaultFormat.replace(".", cfg.decimalPoint));
        }
    }

    // byte
    @Override
    public void addField(byte n) {
        writeSeparator();
        super.addField(n);
    }
    // short
    @Override
    public void addField(short n) {
        writeSeparator();
        super.addField(n);
    }
    // integer
    @Override
    public void addField(int n) {
        writeSeparator();
        super.addField(n);
    }

    // int(n)
    @Override
    public void addField(Integer n, int length, boolean isSigned) {
        writeSeparator();
        super.addField(n, length, isSigned);
    }

    // long
    @Override
    public void addField(long n) {
        writeSeparator();
        super.addField(n);
    }

    // boolean
    @Override
    public void addField(boolean b) {
        writeSeparator();
        super.addRawData(b ? cfg.booleanTrue : cfg.booleanFalse);
    }

    // float
    @Override
    public void addField(float f) {
        writeSeparator();
        String defaultFormat = Float.toString(f);
        addRawData(usesDefaultDecimalPoint ? defaultFormat : defaultFormat.replace(".", cfg.decimalPoint));
        if (shouldWarnWhenUsingFloat) {
            shouldWarnWhenUsingFloat = false;  // only warn once per record
            LOGGER.warn("Using float or double and removal of decimal point may result in undefined output");
        }
    }

    // double
    @Override
    public void addField(double d) {
        writeSeparator();
        String defaultFormat = Double.toString(d);
        addRawData(usesDefaultDecimalPoint ? defaultFormat : defaultFormat.replace(".", cfg.decimalPoint));
        if (shouldWarnWhenUsingFloat) {
            shouldWarnWhenUsingFloat = false;  // only warn once per record
            LOGGER.warn("Using float or double and removal of decimal point may result in undefined output");
        }
    }

    // UUID
    @Override
    public void addField(UUID n) {
        writeSeparator();
        if (n != null) {
            addRawData(stringQuote);
            super.addField(n);
            addRawData(stringQuote);
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(ByteArray b, int length) {
        writeSeparator();
        if (b != null) {
            addRawData(stringQuote);
            super.addField(b, length);
            addRawData(stringQuote);
        }
    }

    // raw
    @Override
    public void addField(byte[] b, int length) {
        writeSeparator();
        if (b != null) {
            addRawData(stringQuote);
            super.addField(b, length);
            addRawData(stringQuote);
        }
    }

    // converters for DAY und TIMESTAMP
    @Override
    public void addField(Calendar t, boolean hhmmss, int length) {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            addRawData(calendarFormat.format(t));
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        }
    }
    @Override
    public void addField(LocalDate t) {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            addRawData(dateFormat.print(t));
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        }
    }

    @Override
    public void addField(LocalDateTime t, boolean hhmmss, int length) {
        writeSeparator();
        if (t != null) {
            if (cfg.datesQuoted)
                addRawData(stringQuote);
            addRawData(dateTimeFormat.print(t));
            if (cfg.datesQuoted)
                addRawData(stringQuote);
        }
    }
    
    @Override
    public void startMap(int currentMembers, int indexID) {
        if (cfg.mapStart != null) {
            super.addRawData(cfg.mapStart);
            recordStart = true;
        }
    }

    @Override
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) {
        if (cfg.arrayStart != null) {
            super.addRawData(cfg.arrayStart);
            recordStart = true;
        }
    }

    @Override
    public void terminateArray() {
        super.addRawData(cfg.arrayEnd);
        recordStart = true;
    }
    
    @Override
    public void terminateMap() {
        super.addRawData(cfg.mapEnd);
        recordStart = true;
    }

    @Override
    public void addField(BonaPortable obj) {
        if (obj != null) {
            if (cfg.objectStart != null) {
                super.addRawData(cfg.objectStart);
                recordStart = true;
            }
            // do all fields (now includes terminator)
            obj.serializeSub(this);
            super.addRawData(cfg.objectEnd);
            recordStart = true;
        }
    }
}
