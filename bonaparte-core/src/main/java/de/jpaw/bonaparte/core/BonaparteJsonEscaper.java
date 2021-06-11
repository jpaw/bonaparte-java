package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.jpaw.jsonext.ExtendedJsonEscaperForAppendables;

public class BonaparteJsonEscaper extends ExtendedJsonEscaperForAppendables {

    // static utility method: serialize Object (single field)
    public static String asJson(Object obj) {
        return asJson(obj, true);
    }
    public static String asJson(Object obj, boolean writeNulls) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(100);
        try {
            new BonaparteJsonEscaper(buff, writeNulls).outputJsonElement(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toString();
    }

    // static utility method: serialize Object (single field)
    public static String asJson(Map<String, Object> obj) {
        return asJson(obj, true);
    }
    public static String asJson(Map<String, Object> obj, boolean writeNulls) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(100);
        try {
            new BonaparteJsonEscaper(buff, writeNulls).outputJsonObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toString();
    }

    // static utility method: serialize Object (array)
    public static String asJson(List<?> obj) {
        return asJson(obj, true);
    }
    public static String asJson(List<?> obj, boolean writeNulls) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(100);
        try {
            new BonaparteJsonEscaper(buff, writeNulls).outputJsonArray(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toString();
    }

    public BonaparteJsonEscaper(Appendable appendable) {
        super(appendable);
    }

    public BonaparteJsonEscaper(Appendable appendable, boolean writeNulls) {
        super(appendable, writeNulls, false, false);
    }

//  private JsonComposer bonaparteJsonComposer = null;
//    public BonaparteJsonEscaper(Appendable appendable, JsonComposer bonaparteJsonComposer) {
//        super(appendable);
//        this.bonaparteJsonComposer = bonaparteJsonComposer;  // avoid recursive construction of new objects
//    }

    @Override
    public void outputJsonElement(Object obj) throws IOException {
        if (obj instanceof BonaCustom) {
//            if (bonaparteJsonComposer == null) {
//                bonaparteJsonComposer = new JsonComposer(appendable, false, this);
//            }
//            // output as Json
//            bonaparteJsonComposer.writeObject((BonaCustom)obj);
            new JsonComposer(appendable, false, this).writeObject((BonaCustom)obj);
            return;
        }
        super.outputJsonElement(obj);
    }
}
