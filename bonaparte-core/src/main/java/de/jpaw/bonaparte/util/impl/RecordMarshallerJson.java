package de.jpaw.bonaparte.util.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.util.IMarshaller;
import de.jpaw.json.JsonParser;
import de.jpaw.util.ApplicationException;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

public class RecordMarshallerJson implements IMarshaller {

    @Override
    public String getContentType() {
        return MimeTypes.MIME_TYPE_JSON;
    }

    @Override
    public ByteArray marshal(BonaPortable request) {
        return ByteArray.fromString(JsonComposer.toJsonString(request));
//        return ByteArray.fromString(BonaparteJsonEscaper.asJson(request));
    }

    @Override
    public BonaPortable unmarshal(ByteBuilder buffer) throws ApplicationException {
        final JsonParser jp = new JsonParser(new String(buffer.getCurrentBuffer(), 0, buffer.length(), ByteArray.CHARSET_UTF8), false);
        return MapParser.asBonaPortable(jp.parseObject(), StaticMeta.OUTER_BONAPORTABLE);
    }
}
