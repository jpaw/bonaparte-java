package de.jpaw.bonaparte.core;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;

public abstract class AbstractMessageParser<E extends Exception> extends Settings implements MessageParser<E> {

    @Override
    public char readPrimitiveCharacter(MiscElementaryDataItem di) throws E {
        return readCharacter(di).charValue();
    }

    @Override
    public boolean readPrimitiveBoolean(MiscElementaryDataItem di) throws E {
        return readBoolean(di).booleanValue();
    }

    @Override
    public double readPrimitiveDouble(BasicNumericElementaryDataItem di) throws E {
        return readDouble(di).doubleValue();
    }

    @Override
    public float readPrimitiveFloat(BasicNumericElementaryDataItem di) throws E {
        return readFloat(di).floatValue();
    }

    @Override
    public long readPrimitiveLong(BasicNumericElementaryDataItem di) throws E {
        return readLong(di).longValue();
    }

    @Override
    public int readPrimitiveInteger(BasicNumericElementaryDataItem di) throws E {
        return readInteger(di).intValue();
    }

    @Override
    public short readPrimitiveShort(BasicNumericElementaryDataItem di) throws E {
        return readShort(di).shortValue();
    }

    @Override
    public byte readPrimitiveByte(BasicNumericElementaryDataItem di) throws E {
        return readByte(di).byteValue();
    }

    @Override
    public Integer readEnum(EnumDataItem edi, BasicNumericElementaryDataItem di) throws E {
        return readInteger(di);
    }
    @Override
    public String readEnum(EnumDataItem edi, AlphanumericElementaryDataItem di) throws E {
        return readString(di);
    }
}
