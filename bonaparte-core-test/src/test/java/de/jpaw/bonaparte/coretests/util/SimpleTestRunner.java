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

package de.jpaw.bonaparte.coretests.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.testrunner.MultiTestRunner;

/**
 * The SimpleTestRunner class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This class performs some simple serialization and subsequent deserialization.
 *          It compares the results produced by different serializers.
 *          It also compares the deserialized object with the original object.
 *          Caveat is that the compare function must be verified separately, because a mismatch
 *          could be a problem in the compare function as well. Moreover, success could also
 *          mistakenly reported for incorrect results, if the compare functions returns true
 *          too often.
 */

public class SimpleTestRunner {
    static private final Charset defaultCharset = Charset.forName("UTF-8");          // always use UTF-8 unless explicitly requested differently

    static private void dumpToFile(String filename, byte [] data) throws Exception {
        OutputStream stream = new FileOutputStream(filename);
        stream.write(data);
        stream.close();
    }

    // convert a BonaPortable to byte [] and back
    static public BonaPortable runThroughByteArray(BonaPortable src) throws MessageParserException {
        int srcHash = src.hashCode();
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.setWriteCRs(false);         // ensure the test is valid under Windows as well...
        bac.reset();
        bac.writeRecord(src);
        byte [] bacResult = bac.getBytes();
        MessageParser<MessageParserException> w2 = new ByteArrayParser(bacResult, 0, -1);
        BonaPortable dst2 = w2.readRecord();
        assert dst2.getClass() == src.getClass() : "returned obj is of wrong type (ByteArrayParser)"; // assuming we have one class loader only
        assert src.equals(dst2) : "returned obj is not equal to original one (ByteArrayParser)";
        // verify the hashCodes
        assert dst2.hashCode() == srcHash : "hash code differs for dst2";
        return dst2;
    }

    // convert a BonaPortable to StringBuilder and back
    static public BonaPortable runThroughStringBuilder(BonaPortable src) throws MessageParserException {
        int srcHash = src.hashCode();
        StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder());
        sbc.setWriteCRs(false);         // ensure the test is valid under Windows as well...
        sbc.reset();
        sbc.writeRecord(src);
        byte [] sbcResult = sbc.getBytes();
        StringBuilder work = new StringBuilder(new String (sbcResult, defaultCharset));
        MessageParser<MessageParserException> w1 = new StringBuilderParser(work, 0, -1);
        BonaPortable dst1 = w1.readRecord();
        assert dst1.getClass() == src.getClass() : "returned obj is of wrong type (StringBuilderParser)"; // assuming we have one class loader only
        assert src.equals(dst1) : "returned obj is not equal to original one (StringBuilderParser)";
        // verify the hashCodes
        assert dst1.hashCode() == srcHash : "hash code differs for dst1";
        return dst1;
    }

    static public void run(BonaPortable src, boolean doDumpToFile) throws Exception {
        int srcHash = src.hashCode();
        System.out.println("");
        System.out.println("Test " + src.get$PQON() + " (hash " + srcHash + ") starting:");

        /************************************************************************************
         *
         * Part Ia: Bonaparte format (external) => serialization
         *
         ***********************************************************************************/

        System.out.println("composer StringBuilder");
        StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder());
        sbc.setWriteCRs(false);         // ensure the test is valid under Windows as well...
        sbc.reset();
        sbc.writeRecord(src);
        byte [] sbcResult = sbc.getBytes();

        System.out.println("composer ByteArray");
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.setWriteCRs(false);         // ensure the test is valid under Windows as well...
        bac.reset();
        bac.writeRecord(src);
        byte [] bacResult = bac.getBytes();

        System.out.println("Length with SBC is " + sbcResult.length + ", length with BAC is " + bacResult.length);
        if (doDumpToFile) {
            dumpToFile("/tmp/" + src.get$PQON() + "-dump-sbc.bin", sbcResult);
            dumpToFile("/tmp/" + src.get$PQON() + "-dump-bac.bin", bacResult);
        }

        assert sbcResult.length == bacResult.length : "produced byte data should have the same length";
        assert Arrays.equals(sbcResult, bacResult) : "produced byte data should be identical";

        /************************************************************************************
         *
         * Part Ib: Bonaparte format (external) => deserialization and compare
         *
         ***********************************************************************************/

        System.out.println("parser StringBuilder");
        StringBuilder work = new StringBuilder(new String (bacResult, defaultCharset));
        MessageParser<MessageParserException> w1 = new StringBuilderParser(work, 0, -1);
        BonaPortable dst1 = w1.readRecord();
        assert dst1.getClass() == src.getClass() : "returned obj is of wrong type (StringBuilderParser)"; // assuming we have one class loader only
        assert src.equals(dst1) : "returned obj is not equal to original one (StringBuilderParser)";
        // verify the hashCodes
        assert dst1.hashCode() == srcHash : "hash code differs for dst1";

        // alternate deserializer
        System.out.println("parser ByteArray");
        MessageParser<MessageParserException> w2 = new ByteArrayParser(sbcResult, 0, -1);
        BonaPortable dst2 = w2.readRecord();
        assert dst2.getClass() == src.getClass() : "returned obj is of wrong type (ByteArrayParser)"; // assuming we have one class loader only
        assert src.equals(dst2) : "returned obj is not equal to original one (ByteArrayParser)";
        // verify the hashCodes
        assert dst2.hashCode() == srcHash : "hash code differs for dst2";

        /************************************************************************************
         *
         * Part IIa: Java externalization support: serialize
         *
         ***********************************************************************************/

        System.out.println("externalizer");
        ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);
        ObjectOutputStream o = new ObjectOutputStream(fos);
        o.writeObject(src);
        o.close();
        byte[] result = fos.toByteArray();
        if (doDumpToFile)
            dumpToFile("/tmp/" + src.get$PQON() + "-dump-ext.bin", result);
        System.out.println("Externalization: Length of buffer is " + result.length);

        /************************************************************************************
         *
         * Part IIb: Java externalization support: deserialize
         *
         ***********************************************************************************/

        System.out.println("deexternalizer");
        ByteArrayInputStream fis = new ByteArrayInputStream(result);
        ObjectInputStream in = new ObjectInputStream(fis);
        Object xdst = in.readObject();
        assert xdst.getClass() == src.getClass() : "returned obj is of wrong type (deexternalizer)"; // assuming we have one class loader only
        BonaPortable dst3 = (BonaPortable)xdst;
        assert src.equals(dst3) : "returned obj is not equal to original one (deexternalizer)";
        // verify the hashCodes
        assert dst3.hashCode() == srcHash : "hash code differs for dst3";

        /************************************************************************************
        *
        * Part IIIa: Java compact: serialize
        *
        ***********************************************************************************/

       System.out.println("compact");
       CompactByteArrayComposer cbac = new CompactByteArrayComposer(10000, false);
       cbac.writeRecord(src);
       byte[] cbacResult = cbac.getBuilder().getBytes();
       if (doDumpToFile)
           dumpToFile("/tmp/" + src.get$PQON() + "-dump-compact.bin", cbacResult);
       System.out.println("compact: Length of buffer is " + cbacResult.length);

       System.out.println("compact2");
       ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
       DataOutputStream dataOut = new DataOutputStream(baos);
       CompactComposer cc = new CompactComposer(dataOut, false);
       cc.reset();
       cc.writeRecord(src);
       dataOut.flush();
       byte [] ccResult = baos.toByteArray();
//       System.out.println(ByteUtil.dump(ccResult, 100));
//       System.out.println(ByteUtil.dump(cbacResult, 100));
       assert(ccResult.length == cbacResult.length);
       assert Arrays.equals(ccResult, cbacResult) : "produced byte data should be identical";


       /************************************************************************************
        *
        * Part IIIb: Java compact: deserialize
        *
        ***********************************************************************************/

       System.out.println("decompacter");
       CompactByteArrayParser cbap = new CompactByteArrayParser(cbacResult, 0, -1);
       BonaPortable dst33 = cbap.readRecord();
       assert dst33.getClass() == src.getClass() : "returned obj is of wrong type (decompacter)"; // assuming we have one class loader only
       assert src.equals(dst33) : "returned obj is not equal to original one (decompacter)";
       // verify the hashCodes
       assert dst33.hashCode() == srcHash : "hash code differs for dst3";

       // repeat - the new testrunner
       MultiTestRunner.serDeserMulti(src, null);
    }
}
