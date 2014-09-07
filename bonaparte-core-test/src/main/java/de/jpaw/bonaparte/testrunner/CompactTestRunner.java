package de.jpaw.bonaparte.testrunner;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.testng.Assert;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.CompactComposer;

/** Compact composer, using the alternate output channel (Stream instead of buffer). */
public class CompactTestRunner extends CompactByteArrayTestRunner {
    
    @Override
    public byte[] serializationTest(BonaCustom src, byte[] expectedResult) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
        DataOutputStream dataOut = new DataOutputStream(baos);
        CompactComposer bac = new CompactComposer(dataOut, false);
        bac.writeRecord(src);
        dataOut.flush();
        byte [] result = baos.toByteArray();
        if (expectedResult != null)
            Assert.assertEquals(result, expectedResult);
        return result;
    }

}
