package de.jpaw.bonaparte.core;

import java.util.Locale;

import de.jpaw.bonaparte.pojos.meta.LocaleAdapter;

public class LocaleAdapterImpl {

    private static String nie(String s) {  // nie: null if empty
        return s == null || s.length() == 0 ? null : s;
    }

    private static String nvl(String s) {
        return s == null ? "(null)" : s;
    }

    public static LocaleAdapter marshal(Locale obj) {
        return new LocaleAdapter(obj.getLanguage(), nie(obj.getCountry()), nie(obj.getVariant()), nie(obj.getScript()));
    }

    public static <E extends Exception> Locale unmarshal(LocaleAdapter so, ExceptionConverter<E> p) throws E {
        try {
            if (so.getCountry() == null)
                return new Locale(so.getLanguage());
            else if (so.getVariant() == null)
                return new Locale(so.getLanguage(), so.getCountry());
            else
                return new Locale(so.getLanguage(), so.getCountry(), so.getVariant());
        } catch (Exception e) {
            throw p.customExceptionConverter("Cannot create Locale for language " + nvl(so.getLanguage())
                    + ", country " + nvl(so.getCountry()) + ", variant " + nvl(so.getVariant()), e);
        }
    }
}
