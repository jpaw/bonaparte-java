package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.util.Map;

import de.jpaw.jsonext.ExtendedJsonEscaperForAppendables;

public class BonaparteJsonEscaper extends ExtendedJsonEscaperForAppendables {

    // static utility method: serialize Object (single field)
    public static String asJson(Object obj) {
        StringBuilder buff = new StringBuilder(100);
        try {
            new BonaparteJsonEscaper(buff).outputJsonElement(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toString();
    }

    // static utility method: serialize Object (single field)
    public static String asJson(Map<String, Object> obj) {
        StringBuilder buff = new StringBuilder(100);
        try {
            new BonaparteJsonEscaper(buff).outputJsonObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toString();
    }

    private JsonComposer bonaparteJsonComposer = null;

    public BonaparteJsonEscaper(Appendable appendable) {
        super(appendable);
    }

    public BonaparteJsonEscaper(Appendable appendable, JsonComposer bonaparteJsonComposer) {
        super(appendable);
        this.bonaparteJsonComposer = bonaparteJsonComposer;  // avoid recursive construction of new objects
    }

    @Override
    public void outputJsonElement(Object obj) throws IOException {
        if (obj instanceof BonaCustom) {
            if (bonaparteJsonComposer == null) {
                bonaparteJsonComposer = new JsonComposer(appendable, false, this);
            }
            // output as Json
            bonaparteJsonComposer.writeObject((BonaCustom)obj);
            return;
        }
        super.outputJsonElement(obj);
    }
}
