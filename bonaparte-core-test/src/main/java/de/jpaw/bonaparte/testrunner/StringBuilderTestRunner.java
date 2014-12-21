package de.jpaw.bonaparte.testrunner;

import org.testng.Assert;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;

public class StringBuilderTestRunner extends AbstractTestrunner<String> {

    @Override
    public String serializationTest(BonaCustom src, String expectedResult) throws Exception {
        StringBuilder buffer = new StringBuilder(256);
        StringBuilderComposer bac = new StringBuilderComposer(buffer);
        bac.setWriteCRs(false);			// ensure the test is valid under Windows as well...
        bac.writeRecord(src);
        String result = buffer.toString();
        if (expectedResult != null)
            Assert.assertEquals(result, expectedResult);
        return result;
    }

    @Override
    public BonaPortable deserializationTest(String src, BonaPortable expectedResult) throws Exception {
        StringBuilderParser bap = new StringBuilderParser(src, 0, -1);
        BonaPortable result = bap.readRecord();
        if (expectedResult != null)
            Assert.assertEquals(result, expectedResult);
        return result;
    }
}
