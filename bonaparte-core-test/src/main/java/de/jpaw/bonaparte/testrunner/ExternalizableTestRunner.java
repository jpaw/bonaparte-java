package de.jpaw.bonaparte.testrunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Assertions;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ExternalizableComposer;
import de.jpaw.bonaparte.core.ExternalizableParser;

public class ExternalizableTestRunner extends AbstractTestrunner<byte[]> {

    @Override
    public byte[] serializationTest(BonaCustom src, byte[] expectedResult) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
        ObjectOutputStream dataOut = new ObjectOutputStream(baos);
        ExternalizableComposer bac = new ExternalizableComposer(dataOut);
        bac.writeRecord(src);
        dataOut.flush();
        byte [] result = baos.toByteArray();
        if (expectedResult != null)
            Assertions.assertEquals(result, expectedResult);
        return result;
    }

    @Override
    public BonaPortable deserializationTest(byte[] src, BonaPortable expectedResult) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(src);
        ObjectInputStream dataIn = new ObjectInputStream(bais);
        ExternalizableParser bap = new ExternalizableParser(dataIn);
        BonaPortable result = bap.readRecord();
        if (expectedResult != null)
            Assertions.assertEquals(result, expectedResult);
        return result;
    }
}
