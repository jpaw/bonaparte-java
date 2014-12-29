package de.jpaw.bonaparte.util.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.util.QuickConverter;

public class RecordConverterBonaString implements QuickConverter<String> {
    private final Boolean writeCRs;
    
    /** Creates a record converter for default settings. */
    public RecordConverterBonaString() {
        this.writeCRs = null;
    }
    
    /** Creates a record converter with the option to configure portable CR/LF settings. */
    public RecordConverterBonaString(Boolean writeCRs) {
        this.writeCRs = writeCRs;
    }
    
    
    /** Serializes an object using the "almost readable" notation into a String, including record terminators. */
    @Override
    public String marshal(BonaPortable obj) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(INITIAL_BUFFER_SIZE); // guess some initial size
        StringBuilderComposer composer = new StringBuilderComposer(buff);
        if (writeCRs != null)
            composer.setWriteCRs(writeCRs);
        composer.writeRecord(obj);
        return buff.toString();
    }

    /** Parses a String into a specific BonaPortable. */
    @Override
    public <T extends BonaPortable> T unmarshal(String data, Class<T> expectedClass) throws MessageParserException {
        if (data == null || data.length() == 0)
            return null;
        StringBuilderParser parser = new StringBuilderParser(data, 0, -1);
        return expectedClass.cast(parser.readRecord());
    }
}
