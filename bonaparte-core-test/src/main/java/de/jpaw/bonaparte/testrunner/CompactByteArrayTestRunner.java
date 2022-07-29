package de.jpaw.bonaparte.testrunner;

import org.junit.jupiter.api.Assertions;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.util.ByteBuilder;

/** Test runner using the CompactByteArrayComposer / Parser. */
public class CompactByteArrayTestRunner extends AbstractTestrunner<byte[]> {

    @Override
    public byte[] serializationTest(BonaCustom src, byte[] expectedResult) throws Exception {
        ByteBuilder buffer = new ByteBuilder();
        CompactByteArrayComposer bac = new CompactByteArrayComposer(buffer, false);
        bac.setWriteCRs(false);         // ensure the test is valid under Windows as well...
        bac.writeRecord(src);
        byte[] result = buffer.getBytes();
        if (expectedResult != null)
            Assertions.assertEquals(result, expectedResult);
        return result;
    }

    @Override
    public BonaPortable deserializationTest(byte[] src, BonaPortable expectedResult) throws Exception {
        CompactByteArrayParser bap = new CompactByteArrayParser(src, 0, -1);
        BonaPortable result = bap.readRecord();
        if (expectedResult != null)
            Assertions.assertEquals(result, expectedResult);
        return result;
    }

}
