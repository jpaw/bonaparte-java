package de.jpaw.bonaparte.jpa.converters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.AttributeConverter;

// returns unified strings
// @Converter(autoApply = true)
public class ConverterUnifyLong implements AttributeConverter<Long, Long> {
    private static final Map<Long, Long> unifiedLongs = new ConcurrentHashMap<>(1000);

    @Override
    public Long convertToDatabaseColumn(final Long obj) {
        return obj;
    }

    @Override
    public Long convertToEntityAttribute(final Long data) {
        if (data == null)
            return null;
        return unifiedLongs.computeIfAbsent(data, d -> d);
    }

    /** Clears the data map. */
    public static void clear() {
        unifiedLongs.clear();
    }

    /** Clears the data map. */
    public static int size() {
        return unifiedLongs.size();
    }
}
