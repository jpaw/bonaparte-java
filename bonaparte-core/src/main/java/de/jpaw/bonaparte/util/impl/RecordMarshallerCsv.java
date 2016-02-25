package de.jpaw.bonaparte.util.impl;

import java.io.IOException;
import java.nio.charset.Charset;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer2;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.core.StringCSVParser;
import de.jpaw.bonaparte.util.IMarshaller;
import de.jpaw.util.ApplicationException;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

public class RecordMarshallerCsv implements IMarshaller {
    private final CSVConfiguration cfg;
    private final Charset cs;

    public RecordMarshallerCsv(CSVConfiguration cfg, Charset cs) {
        this.cfg = cfg;
        this.cs = cs == null ? ByteArray.CHARSET_UTF8 : cs;
    }

    @Override
    public String getContentType() {
        return MimeTypes.MIME_TYPE_CSV;
    }

    @Override
    public ByteArray marshal(BonaPortable request) throws IOException {
        StringBuilder sb = new StringBuilder(1000);
        CSVComposer2 cc = new CSVComposer2(sb, cfg);
        cc.writeRecord(request);
        return ByteArray.fromString(sb.toString(), cs);
    }

    @Override
    public BonaPortable unmarshal(ByteBuilder buffer) throws ApplicationException {
        StringCSVParser cp = new StringCSVParser(cfg, new String(buffer.getCurrentBuffer(), 0, buffer.length(), cs));
        return cp.readRecord();
    }
}
