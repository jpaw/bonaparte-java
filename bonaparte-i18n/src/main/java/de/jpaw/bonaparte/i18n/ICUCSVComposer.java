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
package de.jpaw.bonaparte.i18n;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;

import de.jpaw.bonaparte.core.CSVComposer2;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
/**
 * The CSVComposer class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implements the serialization for the bonaparte format using a character Appendable, for CSV output
 */

public class ICUCSVComposer extends CSVComposer2 {

    protected final NumberFormat numberFormat2;             // locale's default format for formatting primitive types
    protected final NumberFormat bigDecimalFormat2;         // locale's default format for formatting BigDecimal, covers decimal point and sign

    // use the standard Java Locale
    public ICUCSVComposer(Appendable work, CSVConfiguration cfg) {
        super(work, cfg);
        this.bigDecimalFormat2 = NumberFormat.getInstance(cfg.locale);
        this.bigDecimalFormat2.setGroupingUsed(cfg.useGrouping);
        this.numberFormat2 = (NumberFormat)bigDecimalFormat2.clone();
    }

    // use the extended Locale
    public ICUCSVComposer(Appendable work, CSVConfiguration cfg, ULocale ulocale) {
        super(work, cfg);
        this.bigDecimalFormat2 = NumberFormat.getInstance(ulocale);
        this.bigDecimalFormat2.setGroupingUsed(cfg.useGrouping);
        this.numberFormat2 = (NumberFormat)bigDecimalFormat2.clone();
    }

    // override the methods using the CSVComposer numberFormats

    // BigInteger(n)
    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) throws IOException {
        writeSeparator();
        if (n != null) {
            addRawData(numberFormat2.format(n));
        } else {
            writeNull();
        }
    }
    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        writeSeparator();
        if (n != null) {
            // use standard locale formatter
            bigDecimalFormat2.setMaximumFractionDigits(n.scale());
            bigDecimalFormat2.setMinimumFractionDigits(n.scale());
            addRawData(bigDecimalFormat2.format(n));
        }
    }

    @Override
    protected void outputFixedPointScaledInt(BasicNumericElementaryDataItem di, long n) throws IOException {
        writeSeparator();
        if (di.getDecimalDigits() == 0 || cfg.removePoint4BD) {
            addRawData(numberFormat2.format(n));
        } else {
            int scale = di.getDecimalDigits();
            // use standard locale formatter to get the localized . or ,
            bigDecimalFormat2.setMaximumFractionDigits(scale);
            bigDecimalFormat2.setMinimumFractionDigits(scale);
            addRawData(bigDecimalFormat2.format(BigDecimal.valueOf(n, scale)));
        }
    }
    // float
    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        writeSeparator();
        addRawData(numberFormat2.format(f));            // format using the locale's approach
    }

    // double
    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        writeSeparator();
        addRawData(numberFormat2.format(d));            // format using the locale's approach
    }
}
