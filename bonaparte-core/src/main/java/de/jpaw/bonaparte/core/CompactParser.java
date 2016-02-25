package de.jpaw.bonaparte.core;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.util.ByteArray;

public class CompactParser extends AbstractCompactParser<IOException> {
    protected final DataInput in;
    private int pushedBack = -1;

    public static void deserialize(BonaPortable obj, DataInput _in) throws IOException {
        obj.deserialize(new CompactParser(_in));
    }

    public CompactParser(DataInput in) {
        super();
        this.in = in;
    }

    /**************************************************************************************************
     * Deserialization goes here. Code below does not use the ByteBuilder class,
     * but reads from the byte[] directly
     **************************************************************************************************/

    @Override
    protected IOException newMPE(int errorCode, String msg) {
        return new IOException(String.format("Error " + errorCode + " in " + currentClass + ": " + msg));
    }

    @Override
    protected BonaPortable createObject(String classname) throws IOException {           // same method - overloading required for possible exception mapping
        try {
            return BonaPortableFactory.createObject(classname);
        } catch (MessageParserException e) {
            throw new IOException(e);  // wrap exception
        }
    }

    @Override
    protected BigDecimal checkAndScale(BigDecimal num, NumericElementaryDataItem di) throws IOException {
        try {
            return BigDecimalTools.checkAndScale(num, di, -1, currentClass);
        } catch (MessageParserException e) {
            throw new IOException(e);  // wrap exception
        }
    }


    @Override
    protected boolean atEnd() throws IOException {
        try {
            pushback(needToken());      // DataInput has no EOF method - specific types of DataInput could use better ways to determine end of input
        } catch (EOFException e) {
            return true;
        }
        return false;
    }

    @Override
    protected int needToken() throws IOException {
        if (pushedBack >= 0) {
            int c = pushedBack;
            pushedBack = -1;
            return c;
        }
        return 0xff & in.readUnsignedByte();        // workaround hazelcast 3.4.0/1 bug
    }

    @Override
    protected void needToken(int c) throws IOException {
        int d = needToken();
        if (c != d) {
            throw new IOException(String.format("expected 0x%02x, got 0x%02x in class %s", c, d, currentClass));
        }
    }

    @Override
    protected void pushback(int c) {
        pushedBack = c;
    }

    @Override
    protected void skipBytes(int howMany) throws IOException {
        in.skipBytes(howMany);
    }

    @Override
    protected char readChar() throws IOException {
        return in.readChar();
    }

    @Override
    protected int readFixed2ByteInt() throws IOException {
        return in.readShort();
    }

    @Override
    protected int readFixed3ByteInt() throws IOException {
        int nn = in.readByte() << 16;             // does sign-extend as required
        return nn | in.readUnsignedShort();
    }

    @Override
    protected int readFixed4ByteInt() throws IOException {
        return in.readInt();
    }

    @Override
    protected long readFixed6ByteLong() throws IOException {
        int nn1 = in.readShort();
        return (long)nn1 << 32 | (in.readInt() & 0xffffffffL);
    }
    @Override
    protected long readFixed8ByteLong() throws IOException {
        return in.readLong();
    }


    @Override
    protected byte [] readBytes(int len) throws IOException {
        if (len == 0)
            return EMPTY_BYTE_ARRAY;
        byte [] data = new byte [len];
        in.readFully(data);
        return data;
    }

    @Override
    protected ByteArray readByteArray(int len) throws IOException {
        return ByteArray.fromDataInput(in, len);
    }


    @Override
    protected String readISO(int len) throws IOException {
        char data [] = new char [len];
        for (int i = 0; i < len; ++i)
            data[i] = (char)(0xff & in.readUnsignedByte());     // workaround hazelcast 3.4.x bug
        return new String(data);
    }

    @Override
    protected String readUTF16(int len) throws IOException {
        char data [] = new char [len];
        for (int i = 0; i < len; ++i)
            data[i] = in.readChar();
        return new String(data);
    }

    @Override
    protected String readUTF8(int len) throws IOException {
        final byte [] tmp = new byte [len];
        in.readFully(tmp);
        try {
            return new String(tmp, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);      // should never happen, CHARSET_UTF16 is guaranteed to exist
        }
    }

}
