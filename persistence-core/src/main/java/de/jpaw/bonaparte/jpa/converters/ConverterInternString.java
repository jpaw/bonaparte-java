package de.jpaw.bonaparte.jpa.converters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.AttributeConverter;

// returns "interned" strings
// @Converter(autoApply = true)
public class ConverterInternString implements AttributeConverter<String, String> {
    private static final Map<String, String> internedStrings = new ConcurrentHashMap<>(1000);

    @Override
    public String convertToDatabaseColumn(final String obj) {
        return obj;
    }

    @Override
    public String convertToEntityAttribute(final String data) {
        if (data == null)
            return null;
        return internedStrings.computeIfAbsent(data, d -> d.intern());
    }

    /** Clears the data map. */
    public static void clear() {
        internedStrings.clear();
    }

    /** Clears the data map. */
    public static int size() {
        return internedStrings.size();
    }
}
