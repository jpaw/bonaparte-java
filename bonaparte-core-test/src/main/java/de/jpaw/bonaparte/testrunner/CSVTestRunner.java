package de.jpaw.bonaparte.testrunner;

import org.junit.jupiter.api.Assertions;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVComposer2;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.FixedWidthComposer;
import de.jpaw.bonaparte.core.StringCSVParser;

/** Due to limitations of the CSV format, this test will not work for all BonaPortables. */
public class CSVTestRunner extends AbstractTestrunner<String> {
    public final CSVConfiguration cfg;
    public final boolean fixedLength;
    public final boolean useComposer2;

    public CSVTestRunner(CSVConfiguration cfg, boolean useComposer2) {
        this.cfg = cfg;
        this.fixedLength = cfg.separator.length() == 0;
        this.useComposer2 = useComposer2;
    }

    @Override
    public String serializationTest(BonaCustom src, String expectedResult) throws Exception {
        StringBuilder buffer = new StringBuilder(256);
        CSVComposer csvc = fixedLength ? new FixedWidthComposer(buffer, cfg) : useComposer2 ? new CSVComposer2(buffer, cfg) : new CSVComposer(buffer, cfg);
        csvc.writeRecord(src);
        csvc.writeRecord(src);
        String result = buffer.toString();
        if (expectedResult != null)
            Assertions.assertEquals(result, expectedResult);
        return result;
    }

    @Override
    public BonaPortable deserializationTest(String src, BonaPortable expectedResult) throws Exception {
        StringCSVParser bap = new StringCSVParser(cfg, src);
        BonaPortable result = bap.readRecord();
        if (expectedResult != null)
            Assertions.assertEquals(result, expectedResult);
        return result;
    }
}
