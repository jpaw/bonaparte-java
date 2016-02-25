package de.jpaw.bonaparte.api.codecs.impl;

import de.jpaw.bonaparte.api.codecs.IMessageEncoder;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.util.ByteArray;

public class JsonEncoder<O extends BonaPortable> implements IMessageEncoder<O, byte []> {

    @Override
    public byte[] encode(O obj, ObjectReference di) {
        return BonaparteJsonEscaper.asJson(obj).getBytes(ByteArray.CHARSET_UTF8);
    }
}
