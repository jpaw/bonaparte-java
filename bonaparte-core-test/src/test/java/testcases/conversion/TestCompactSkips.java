package testcases.conversion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.CompactConstants;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.very.compact.Complex;
import de.jpaw.util.ByteUtil;

public class TestCompactSkips {

    public class CBAPTester extends CompactByteArrayParser {

        public CBAPTester(byte[] buffer, int offset, int length) {
            super(buffer, offset, length);
        }

        public void validateEOF() throws Exception {
            require(0);
            int c;
            try {
                c = needToken();
            } catch (MessageParserException e) {
                return;
            }
            throw new Exception("Expected end of record, got " + Integer.toString(c));
        }

        public void runSkip() {

        }
    }

    @Test
    public void checkConfig() throws Exception {
        Assertions.assertEquals(CompactConstants.SKIP_BYTES.length, 5 * 16 + 4);    // assert that no item has been forgotten / is extra
    }


    @Test
    public void runTest() throws Exception {
        byte [] testdata = CompactByteArrayComposer.marshal(ClassDefinition.meta$$this, ClassDefinition.class$MetaData());   // get some serialized byte array
        System.out.println("Length of serialized data is " + testdata.length + " bytes" + String.format(" (hex %04x)", testdata.length));
        System.out.println(ByteUtil.dump(testdata, 32));
        CBAPTester tester = new CBAPTester(testdata, 1, -1);  // skip the object start token
        tester.eatObjectTerminator();
        tester.validateEOF();
    }

    @Test
    public void runTest2() throws Exception {
        byte [] testdata = CompactByteArrayComposer.marshal(Complex.meta$$this, new Complex(2.7, 3.14));   // get some serialized byte array
        System.out.println("Length of serialized data is " + testdata.length + " bytes" + String.format(" (hex %04x)", testdata.length));
        System.out.println(ByteUtil.dump(testdata, 32));
        CBAPTester tester = new CBAPTester(testdata, 1, -1);  // skip the object start token
        tester.eatObjectTerminator();
        tester.validateEOF();
    }
}
