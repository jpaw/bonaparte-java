package testcases;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;


public class SerializationTest {
    static private final Charset defaultCharset = Charset.forName("UTF-8");          // always use UTF-8 unless explicitly requested differently
    static private boolean doDump = false;

    private void dumpToFile(String filename, byte [] data) throws Exception {
        OutputStream stream = new FileOutputStream(filename);
        stream.write(data);
        stream.close();
    }

    static boolean matchStrings(String a, String b) {
        if (a == null)
            return b == null;
        return a.equals(b);
    }

    static private boolean compareTest1(ClassDefinition a, ClassDefinition b) {
        return a.getIsAbstract() == b.getIsAbstract()
            && a.getIsFinal() == b.getIsFinal()
            && matchStrings(a.getName(), b.getName())
            && matchStrings(a.getRevision(), b.getRevision())
            && a.getNumberOfFields() == b.getNumberOfFields();
    }

    @Test
    public void testObj1StringBuilder() throws Exception {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();

        System.out.println("Test starting: composer Kryo");
        Kryo kryo = new Kryo();
        byte [] buffer = new byte[4000];
        Output output = new Output(buffer);
        kryo.writeObject(output, obj1);
        System.out.println("Length with Kryo is " + output.position());
        output.close();

        
        System.out.println("Test starting: composer Compact");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
        DataOutputStream dataOut = new DataOutputStream(baos);
        CompactComposer cc = new CompactComposer(dataOut, false);
        cc.reset();
        cc.writeRecord(obj1);
        dataOut.flush();
        byte [] ccResult = baos.toByteArray();
        System.out.println("Length with CompactComposer is " + dataOut.size());
        assert(dataOut.size() == ccResult.length);
        
        System.out.println("Test starting: composer ByteArrayCompact");
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(4000, false);
        cbac.writeRecord(obj1);
        byte [] cbacResult = cbac.getBuilder().getBytes();
        System.out.println("Length with ByteArrayCompactComposer is " + cbacResult.length);
        assert(cbacResult.length == dataOut.size());
        assert Arrays.equals(ccResult, cbacResult) : "produced byte data should be identical";
        

        System.out.println("Test starting: composer StringBuilder");
        StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder());
        sbc.reset();
        sbc.writeRecord(obj1);
        byte [] sbcResult = sbc.getBytes();

        System.out.println("Test starting: composer ByteArray");
        MessageComposer<RuntimeException> bac = new ByteArrayComposer();
        bac.writeRecord(obj1);
        byte [] bacResult = sbc.getBytes();

        System.out.println("Length with SBC is " + sbcResult.length + ", length with BAC is " + bacResult.length);
        assert sbcResult.length == bacResult.length : "produced byte data should have the same length";
        assert Arrays.equals(sbcResult, bacResult) : "produced byte data should be identical";

        if (doDump)
            dumpToFile("/tmp/Test1-dump.bin", sbcResult);

        // deserialize again
        StringBuilder work = new StringBuilder(new String (bacResult, defaultCharset));
        MessageParser<MessageParserException> w = new StringBuilderParser(work, 0, -1);
        BonaPortable obj2 = w.readRecord();
        assert obj2 instanceof ClassDefinition : "returned obj is of wrong type (StringBuilderParser)";
        assert compareTest1(obj1, (ClassDefinition)obj2) : "returned obj is not equal to original one (StringBuilderParser)";
        assert obj1.hasSameContentsAs(obj2) : "returned obj is not equal to original one (StringBuilderParser) (own test)";

        // alternate deserializer
        MessageParser<MessageParserException> w2 = new ByteArrayParser(sbcResult, 0, -1);
        BonaPortable obj3 = w2.readRecord();
        assert obj3 instanceof ClassDefinition : "returned obj is of wrong type (ByteArrayParser)";
        assert compareTest1(obj1, (ClassDefinition)obj3) : "returned obj is not equal to original one (ByteArrayParser)";
        assert obj1.hasSameContentsAs(obj3) : "returned obj is not equal to original one (ByteArrayParser) (own test)";

    }
}
