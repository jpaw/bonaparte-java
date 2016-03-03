package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.math.BigDecimal;

import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;

/**
 * The CSVComposer3 class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          CSV composer with implicit fixed scaling of BigDecimal fields, always exactly to the number of decimals specified.
 */

public class CSVComposer3 extends CSVComposer2 {

    public CSVComposer3(Appendable work, CSVConfiguration cfg) {
        super(work, cfg);
    }

    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        writeSeparator();
        if (n != null) {
            if (cfg.removePoint4BD) {
                // use standard BigDecimal formatter, and remove the "." from the output (same as before)
                addRawData(n.setScale(di.getDecimalDigits()).toPlainString().replace(".", ""));
            } else {
                // use standard locale formatter to get the localized . or ,
                bigDecimalFormat.setMaximumFractionDigits(di.getDecimalDigits());
                bigDecimalFormat.setMinimumFractionDigits(di.getDecimalDigits());
                addRawData(bigDecimalFormat.format(n));
            }
        }
    }
}
