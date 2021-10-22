package de.jpaw.bonaparte.core.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.testng.annotations.Test;

import de.jpaw.util.ByteArray;

public class ByteArraySerializationTest {

    private static void dumpToFile(String filename, byte [] data) throws Exception {
        OutputStream stream = new FileOutputStream(filename);
        stream.write(data);
        stream.close();
    }

    static public byte [] serialize(Object x) throws IOException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);
        ObjectOutputStream o = new ObjectOutputStream(fos);
        o.writeObject(x);
        o.close();
        byte[] result = fos.toByteArray();
        System.out.println("Length of buffer is " + result.length);
        return result;
    }

    static Object deserialize(byte [] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream fis = new ByteArrayInputStream(data);
        ObjectInputStream i = new ObjectInputStream(fis);
        Object r = i.readObject();
        i.close();
        return r;
    }

    @Test
    public void testEncodeDecode() throws Exception {
        // test runs with different padding, include the trivial zero length case
        ByteArray test1 = new ByteArray("Hello, world".getBytes("UTF-8"));
        ByteArray test2 = test1.subArray(2, 5);

        byte [] res1 = serialize(test1);
        byte [] res2 = serialize(test2);

        dumpToFile("/tmp/byte-array-1", res1);
        dumpToFile("/tmp/byte-array-2", res2);

        Object r1 = deserialize(res1);
        System.out.println("Got object " + r1.getClass().getName());
        System.out.println("Text is " + new String(((ByteArray)r1).getBytes(), "UTF-8"));
        Object r2 = deserialize(res2);
        System.out.println("Got object " + r2.getClass().getName());
        System.out.println("Text is " + new String(((ByteArray)r2).getBytes(), "UTF-8"));
        assert test1.equals(r1);
        assert test2.equals(r2);
    }
}
