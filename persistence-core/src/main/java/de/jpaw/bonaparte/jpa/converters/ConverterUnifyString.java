package de.jpaw.bonaparte.jpa.converters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.AttributeConverter;

// returns unified strings
// @Converter(autoApply = true)
public class ConverterUnifyString implements AttributeConverter<String, String> {
    private static final Map<String, String> unifiedStrings = new ConcurrentHashMap<>(1000);

    @Override
    public String convertToDatabaseColumn(final String obj) {
        return obj;
    }

    @Override
    public String convertToEntityAttribute(final String data) {
        if (data == null)
            return null;
        return unifiedStrings.computeIfAbsent(data, d -> d);
    }

    /** Clears the data map. */
    public static void clear() {
        unifiedStrings.clear();
    }

    /** Clears the data map. */
    public static int size() {
        return unifiedStrings.size();
    }
}
