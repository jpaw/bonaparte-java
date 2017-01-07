package de.jpaw.bonaparte.testrunner;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.CompactComposer;

public class CompactTest {
    static private void dumpToFile(String filename, byte [] data) throws Exception {
        OutputStream stream = new FileOutputStream(filename);
        stream.write(data);
        stream.close();
    }

    static public void run(BonaPortable src, boolean doDumpToFile) throws Exception {
        int srcHash = src.hashCode();

        System.out.println("compact");
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(10000, false);
        cbac.writeRecord(src);
        byte[] cbacResult = cbac.getBuilder().getBytes();
        if (doDumpToFile)
            dumpToFile("/tmp/" + src.ret$PQON() + "-dump-compact.bin", cbacResult);
        System.out.println("compact: Length of buffer is " + cbacResult.length);
    
        System.out.println("compact2");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
        DataOutputStream dataOut = new DataOutputStream(baos);
        CompactComposer cc = new CompactComposer(dataOut, false);
        cc.reset();
        cc.writeRecord(src);
        dataOut.flush();
        byte [] ccResult = baos.toByteArray();
    //    System.out.println(ByteUtil.dump(ccResult, 100));
    //    System.out.println(ByteUtil.dump(cbacResult, 100));
        assert(ccResult.length == cbacResult.length);
        assert Arrays.equals(ccResult, cbacResult) : "produced byte data should be identical";

        CompactByteArrayParser cbap = new CompactByteArrayParser(cbacResult, 0, -1);
        BonaPortable dst33 = cbap.readRecord();
        assert dst33.getClass() == src.getClass() : "returned obj is of wrong type (decompacter)"; // assuming we have one class loader only
        assert src.equals(dst33) : "returned obj is not equal to original one (decompacter)";
        // verify the hashCodes
        assert dst33.hashCode() == srcHash : "hash code differs for dst3";
    }
}
