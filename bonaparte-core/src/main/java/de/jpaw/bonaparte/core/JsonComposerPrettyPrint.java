package de.jpaw.bonaparte.core;

import java.io.IOException;

public class JsonComposerPrettyPrint extends JsonComposer {

    protected JsonComposerPrettyPrint(StringBuilder buff) {
        super(buff);
    }

    public static String toJsonString(BonaCustom obj) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(4000);
        JsonComposer bjc = new JsonComposerPrettyPrint(buff);
        try {
            bjc.writeRecord(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toString();
    }

    @Override
    protected void newLineandIndentIfPrettyPrint() throws IOException {
        newLine();
        for (int i = indentation; i > 0; --i)
            indent();
    }

    @Override
    protected void spaceIfPrettyPrint() throws IOException {
        out.append(' ');
    }

    protected void indent() throws IOException {
        out.append("    ");
    }
}
