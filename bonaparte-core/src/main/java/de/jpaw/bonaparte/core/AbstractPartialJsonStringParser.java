package de.jpaw.bonaparte.core;

import java.util.List;
import java.util.Map;

import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.json.JsonException;
import de.jpaw.json.JsonParser;

public abstract class AbstractPartialJsonStringParser extends AbstractMessageParser<MessageParserException> implements MessageParser<MessageParserException> {

    abstract protected String getString(FieldDefinition di)throws MessageParserException;
    abstract protected MessageParserException newMPE(int errorCode, FieldDefinition di, String msg);             // construct a suitable exception

    @Override
    public Map<String, Object> readJson(ObjectReference di) throws MessageParserException {
        String tmp = getString(di);
        if (tmp == null)
            return null;
        try {
            return new JsonParser(tmp, false).parseObject();
        } catch (JsonException e) {
            throw newMPE(MessageParserException.JSON_EXCEPTION, di, e.getMessage());
        }
    }

    @Override
    public List<Object> readArray(ObjectReference di) throws MessageParserException {
        String tmp = getString(di);
        if (tmp == null)
            return null;
        try {
            return new JsonParser(tmp, false).parseArray();
        } catch (JsonException e) {
            throw newMPE(MessageParserException.JSON_EXCEPTION, di, e.getMessage());
        }
    }

    @Override
    public Object readElement(ObjectReference di) throws MessageParserException {
        String tmp = getString(di);
        if (tmp == null)
            return null;
        try {
            return new JsonParser(tmp, false).parseElement();
        } catch (JsonException e) {
            throw newMPE(MessageParserException.JSON_EXCEPTION, di, e.getMessage());
        }
    }
}
