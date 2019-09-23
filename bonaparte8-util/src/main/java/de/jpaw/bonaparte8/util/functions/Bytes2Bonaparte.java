package de.jpaw.bonaparte8.util.functions;

import java.nio.charset.Charset;
import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;

public class Bytes2Bonaparte<T extends BonaPortable> implements Function <byte [],T> {
    private final ByteArrayParser parser = new ByteArrayParser(null, 0, 0);
    private final Class<T> classToExpect;

    public Bytes2Bonaparte(Class<T> classToExpect) {
        this.classToExpect = classToExpect;
    }

    public void setCharset(Charset charset) {
        parser.setCharset(charset);
    }

    @Override
    public T apply(byte [] t) {
        if (t == null || t.length == 0)
            return null;
        parser.setSource(t);
        try {
            return parser.readObject(StaticMeta.OUTER_BONAPORTABLE, classToExpect);
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }
}
