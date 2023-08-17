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
import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
/**
 * The FixedWidthComposer class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Fixed width composer, suitable for the SAP IDOC format or COBOL formats.
 *          Numeric values of value NULL will be written as blanks.
 *          There is no specific way to enforce writing nulls as zero or to use the COBOL "BLANK WHEN ZERO", if either of these is required,
 *          the caller must preprocess the data and either convert 0 to null values or null to 0 to achieve the desired effect.
 *          For configuration, the CSV configuration is used.
 *
 *          Not all data types are supported. The supported types are:
 *          - all String types (Ascii/Unicode/Upper/Lower) (nullable)
 *          - Day/Timestamp (not null)
 *          - boolean (not null)
 *          - Number (nullable)
 *          - long (not null, unsigned, assuming a length of 18 characters)
 *          - int (not null, signed, assuming a length of 9 digits)
 *
 *          As we currently don't have all information about the fields passed as parameters, we have to make some assumptions.
 */

public class FixedWidthComposer extends CSVComposer {
    private static final int MAX_SPACE_PADDING = 80;

    // some cached static paddings (for speed)
    private static final String SPACES = "                                                                                "; // 80 characters
    private static final String ZEROES = "000000000000000000"; // 18 characters
    private static final String [] SPACE_PADDINGS = new String [MAX_SPACE_PADDING + 1];
    private static final String [] ZERO_PADDINGS = new String [19];
    static {
        for (int i = 0; i <= MAX_SPACE_PADDING; ++i)
            SPACE_PADDINGS[i] = SPACES.substring(0, i);
        for (int i = 0; i <= 18; ++i)
            ZERO_PADDINGS[i] = ZEROES.substring(0, i);
    }

    // utility method to provide a space padding of suitable length
    private static String getPadding(int length) {
        if (length <= MAX_SPACE_PADDING)
            return SPACE_PADDINGS[length];  // use a cached padding
        // construct a new padding
        StringBuilder b = new StringBuilder(length);
        while (length >= MAX_SPACE_PADDING) {
            b.append(SPACE_PADDINGS[MAX_SPACE_PADDING]);
            length -= MAX_SPACE_PADDING;
        }
        if (length > 0)
            b.append(SPACE_PADDINGS[length]);
        return b.toString();
    }

    private void numericPad(int digits) throws IOException {
        if (digits > 0)
            addRawData(cfg.zeroPadNumbers ? ZERO_PADDINGS[digits] : getPadding(digits));
    }


    public FixedWidthComposer(Appendable work, CSVConfiguration cfg) {
        super(work, cfg);
    }


    @Override
    public void writeNull(FieldDefinition di) throws IOException {
        // examine the type of field in order to write the correct number of spaces
        // we have to cover at least all Wrapper types
        switch (di.getDataCategory()) {
        case BINARY:
            int paddingBytes = getFieldWidth((BinaryElementaryDataItem)di);
            while (paddingBytes >= MAX_SPACE_PADDING) {
                paddingBytes -= MAX_SPACE_PADDING;
                addRawData(SPACE_PADDINGS[MAX_SPACE_PADDING]);
            }
            if (paddingBytes > 0)
                addRawData(SPACE_PADDINGS[paddingBytes]);
            break;
        case TEMPORAL:
            if (di.getDataType().equals("LocalDate")) {
                addRawData(SPACE_PADDINGS[10]);     // FIXME - this length could vary with the selected format
                return;
            }
            if (di.getDataType().equals("LocalTime")) {
                addRawData(SPACE_PADDINGS[8]);      // FIXME - this length could vary with the selected format
                return;
            }
            if (di.getDataType().equals("Instant")) {
                addRawData(SPACE_PADDINGS[18]);
                return;
            }
            if (di.getDataType().equals("LocalDateTime")) {
                addRawData(SPACE_PADDINGS[19]);     // FIXME - this length could vary with the selected format
                return;
            }
            break;
        case BASICNUMERIC:
            addRawData(SPACE_PADDINGS[getFieldWidth((BasicNumericElementaryDataItem)di)]);
            break;
        case MISC:
            if (di.getDataType().equals("Boolean")) {
                addRawData(" ");
                return;
            }
            if (di.getDataType().equals("UUID")) {
                addRawData(SPACE_PADDINGS[36]);
                return;
            }
            break;
        default:
            throw new RuntimeException("writeNull() for category " + di.getDataCategory() + " should be converted by specific methods such as addEnum() etc.");
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) throws IOException {
        if (t != null) {
            outputPaddedNumber(Long.toString(t.getEpochSecond() * 1000L + (long)(t.getNano() / 1000000)), 18);
        } else {
            addRawData(SPACE_PADDINGS[18]);
        }
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        if (n != null) {
            addRawData(n.toString());
        } else {
            addRawData(SPACE_PADDINGS[36]);
        }
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        writeSeparator();
        if (s != null) {
            addRawData(s);
            if (s.length() < di.getLength())
                addRawData(getPadding(di.getLength() - s.length()));
        } else {
            addRawData(getPadding(di.getLength()));
        }
    }

    protected void outputPaddedNumber(String pattern, int totalLength) throws IOException {
        if (cfg.rightPadNumbers) {
            addRawData(pattern);
            numericPad(totalLength - pattern.length());
        } else {
            numericPad(totalLength - pattern.length());
            addRawData(pattern);
        }

    }

    // decimal using TRAILING SIGN
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        writeSeparator();
        int decimals = di.getDecimalDigits();
        if (n != null) {
            // space for the field is length + (1 if decimals > 0 && removePoint4BD == false) + (1 if isSigned)
            boolean isNegative = n.signum() == -1;
            BigDecimal absVal = isNegative ? n.negate() : n;
            if (cfg.removePoint4BD) {
                // use standard BigDecimal formatter, and remove the "." from the output
                outputPaddedNumber(absVal.setScale(decimals).toPlainString().replace(".", ""), di.getTotalDigits());
            } else {
                // use standard locale formatter to get the localized . or ,
                bigDecimalFormat.setMaximumFractionDigits(decimals);
                bigDecimalFormat.setMinimumFractionDigits(decimals);
                outputPaddedNumber(bigDecimalFormat.format(absVal), di.getTotalDigits() + (decimals > 0 ? 1 : 0));
            }
            if (di.getIsSigned()) {
                addRawData(isNegative ? "-" : " ");
            }
        } else {
            // write an appropriate number of spaces
            addRawData(getPadding(getFieldWidth(di)));
        }
    }

    // generic method for all integral types, also supports a fixed decimal point.
    // The point consumes another byte of space, unless it is at position zero.
    private void paddedFixedWidthString(BasicNumericElementaryDataItem di, String s) throws IOException {
        if (di.getDecimalDigits() == 0 || cfg.removePoint4BD) {
            // no decimal point at all
            numericPad(di.getTotalDigits() - s.length());
            addRawData(s);
        } else {
            if (s.length() >= di.getDecimalDigits()) {
                // padding without interruption
                numericPad(di.getTotalDigits() - s.length());
                if (s.length() == di.getDecimalDigits()) {
                    addRawData(".");
                    addRawData(s);
                } else {
                    // 2 real substrings
                    addRawData(s.substring(0, s.length() - di.getTotalDigits()));
                    addRawData(".");
                    addRawData(s.substring(s.length() - di.getTotalDigits()));
                }
            } else {
                // decimal point is somewhere within the padding: split it to make sure second part is definitely zeros!
                numericPad(di.getTotalDigits() - di.getDecimalDigits());
                addRawData(".");
                addRawData(ZERO_PADDINGS[di.getDecimalDigits() - s.length()]);
                addRawData(s);
            }
        }
    }

    // long (SIGNED, LEADING SIGN)
    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        writeSeparator();
        if (di.getIsSigned()) {
            addRawData(n < 0 ? "-" : " ");
            if (n < 0L)
                n = -n;
            if (n < 0L) {
                // must have been MINVAL => special treatment here
                paddedFixedWidthString(di, "9223372036854775808");
                return;
            }
        }
        paddedFixedWidthString(di, Long.toString(n));
    }

    // int (SIGNED, LEADING SIGN)
    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        writeSeparator();
        if (di.getIsSigned()) {
            addRawData(n < 0 ? "-" : " ");
            if (n < 0)
                n = -n;
            if (n < 0) {
                // must have been MINVAL => special treatment here
                paddedFixedWidthString(di, "2147483648");
                return;
            }
        }
        paddedFixedWidthString(di, Integer.toString(n));
    }

    // int (SIGNED, LEADING SIGN)
    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        writeSeparator();
        if (di.getIsSigned()) {
            addRawData(n < 0 ? "-" : " ");
        }
        paddedFixedWidthString(di, Integer.toString(n < 0 ? -(int)n : n));
    }

    // int (SIGNED, LEADING SIGN)
    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        writeSeparator();
        if (di.getIsSigned()) {
            addRawData(n < 0 ? "-" : " ");
        }
        paddedFixedWidthString(di, Integer.toString(n < 0 ? -(int)n : n));
    }

    // int(n) (SIGNED AND UNSIGNED; specific length, LEADING SIGN), null possible
    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) throws IOException {
        writeSeparator();
        if (n == null) {
            addRawData(SPACE_PADDINGS[getFieldWidth(di)]);
        } else {
            if (cfg.rightPadNumbers) {
                String val = n.toString();
                addRawData(val);
                addRawData(SPACE_PADDINGS[di.getTotalDigits() - val.length()]);
            } else {
                if (di.getIsSigned()) {
                    if (n.signum() < 0) {
                        addRawData("-");
                        n = n.negate();
                    } else {
                        addRawData(" ");
                    }
                }
                String val = n.toString();
                numericPad(di.getTotalDigits() - val.length());
                addRawData(val);
            }
        }
    }

    protected int getFieldWidth(BasicNumericElementaryDataItem di) {
        return di.getTotalDigits() + (di.getIsSigned() ? 1 : 0) + (di.getDecimalDigits() > 0  && !cfg.removePoint4BD ? 1 : 0);
    }
    protected int getFieldWidth(BinaryElementaryDataItem di) {
        return (di.getLength() + 2) / 3 * 4;
    }
}
