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
/**
 * The CSVComposer class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Fixed width composer, suitable for the SAP IDOC format or COBOL formats.
 *          Numeric values if value NULL with be written as blanks.
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
    public void addUnicodeString(String s, int length, boolean allowCtrls) throws IOException {
        writeSeparator();
        if (s != null) {
            addRawData(s);
            if (s.length() < length)
                addRawData(getPadding(length - s.length()));
        } else {
            addRawData(getPadding(length));
        }
    }

    // decimal
    @Override
    public void addField(BigDecimal n, int length, int decimals,
            boolean isSigned) throws IOException {
        writeSeparator();
        if (n != null) {
            // space for the field is length + (1 if decimals > 0 && removePoint4BD == false) + (1 if isSigned)
            boolean isNegative = n.signum() == -1;
            BigDecimal absVal = isNegative ? n.negate() : n;
            if (cfg.removePoint4BD) {
                // use standard BigDecimal formatter, and remove the "." from the output
                String pattern = absVal.setScale(decimals).toPlainString().replace(".", "");
                numericPad(length - pattern.length());
                addRawData(pattern);
            } else {
                // use standard locale formatter to get the localized . or ,
                bigDecimalFormat.setMaximumFractionDigits(decimals);
                bigDecimalFormat.setMinimumFractionDigits(decimals);
                String pattern = bigDecimalFormat.format(absVal);
                numericPad(length + (decimals > 0 ? 1 : 0) - pattern.length());
                addRawData(pattern);
            }
            if (isSigned) {
                addRawData(isNegative ? "-" : " ");
            }
        } else {
            // write an appropriate number of spaces
            addRawData(getPadding(length) + (decimals > 0 && !cfg.removePoint4BD ? 1 : 0) + (isSigned ? 1 : 0));
        }
    }
    
    // long
    @Override
    public void addField(long n) throws IOException {
        writeSeparator();
        String val = Long.toString(n);
        numericPad(18 - val.length());
        addRawData(val);
    }
    
    // int
    @Override
    public void addField(int n) throws IOException {
        writeSeparator();
        String val = Integer.toString(n < 0 ? -n : n);
        numericPad(9 - val.length());
        addRawData(val);
        addRawData(n < 0 ? "-" : " ");
    }
}
