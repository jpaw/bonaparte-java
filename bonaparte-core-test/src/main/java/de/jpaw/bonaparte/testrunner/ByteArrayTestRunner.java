package de.jpaw.bonaparte.testrunner;

import org.junit.jupiter.api.Assertions;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;

/** Test runner using the ByteArrayComposer / Parser. */
public class ByteArrayTestRunner extends AbstractTestrunner<byte[]> {

    @Override
    public byte[] serializationTest(BonaCustom src, byte[] expectedResult) throws Exception {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.setWriteCRs(false);         // ensure the test is valid under Windows as well...
        bac.writeRecord(src);
        byte[] result = bac.getBytes();
        if (expectedResult != null)
            Assertions.assertEquals(result, expectedResult);
        return result;
    }

    @Override
    public BonaPortable deserializationTest(byte[] src, BonaPortable expectedResult) throws Exception {
        ByteArrayParser bap = new ByteArrayParser(src, 0, -1);
        BonaPortable result = bap.readRecord();
        if (expectedResult != null)
            Assertions.assertEquals(result, expectedResult);
        return result;
    }
}
