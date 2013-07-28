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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.NumberFormat;
/**
 * The CSVComposer class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implements the serialization for the bonaparte format using a character Appendable, for CSV output
 */

public class ICUCSVComposer extends CSVComposer2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(ICUCSVComposer.class);

    //protected boolean shouldWarnWhenUsingFloat;
    protected final ICUCSVConfiguration cfg2;

    protected final NumberFormat bigDecimalFormat2;          // locale's default format for formatting BigDecimal, covers decimal point and sign

    public ICUCSVComposer(Appendable work, ICUCSVConfiguration cfg) {
        super(work, cfg);
        this.cfg2 = cfg;
        this.bigDecimalFormat2 = NumberFormat.getInstance(cfg.locale);
        this.bigDecimalFormat2.setGroupingUsed(false);                           // this is for interfaces, don't do pretty-printing
        this.numberFormat = (NumberFormat)bigDecimalFormat2.clone();
    }

    // decimal
    @Override
    public void addField(BigDecimal n, int length, int decimals,
            boolean isSigned) throws IOException {
        writeSeparator();
        if (n != null) {
                // use standard locale formatter
                bigDecimalFormat2.setMaximumFractionDigits(n.scale());
                bigDecimalFormat2.setMinimumFractionDigits(n.scale());
                addRawData(bigDecimalFormat2.format(n));
        }
    }

}
