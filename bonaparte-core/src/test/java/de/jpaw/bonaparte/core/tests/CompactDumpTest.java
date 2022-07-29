package de.jpaw.bonaparte.core.tests;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.junit.jupiter.api.Test;

//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.io.Output;

import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.util.ByteUtil;


public class CompactDumpTest {

    @Test
    public void testObj1StringBuilder() throws Exception {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();

//        System.out.println("Test starting: composer Kryo");
//        Kryo kryo = new Kryo();
//        byte [] buffer = new byte[4000];
//        Output output = new Output(buffer);
//        kryo.writeObject(output, obj1);
//        System.out.println("Length with Kryo is " + output.position());
//        output.close();

        System.out.println("Test starting: composer Compact");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
        DataOutputStream dataOut = new DataOutputStream(baos);
        CompactComposer cc = new CompactComposer(dataOut, false);
        cc.reset();
        cc.writeRecord(obj1);
        dataOut.flush();
        System.out.println("Length with CompactComposer (PQON) is " + dataOut.size());
        dataOut.close();
        baos.close();

        CompactByteArrayComposer cbac = new CompactByteArrayComposer(4000, true);
        cbac.writeRecord(obj1);
        System.out.println("Length with CompactByteArrayComposer (ID) is " + cbac.getBuilder().length());

        System.out.println("Test starting: composer Compact");
        baos = new ByteArrayOutputStream(4000);
        dataOut = new DataOutputStream(baos);
        cc = new CompactComposer(dataOut, true);
        cc.reset();
        cc.writeRecord(obj1);
        dataOut.flush();
        System.out.println("Length with CompactComposer (ID) is " + dataOut.size());

        // dump the bytes
        byte [] data = baos.toByteArray();
        System.out.println(ByteUtil.dump(data, 9999));
    }
}
