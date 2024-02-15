package de.jpaw.bonaparte.jpa.converters;

import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.jpa.json.CompactJsonArray;

// persists a serialized object in a BLOB / bytea
@Converter(autoApply = true)
public class ConverterCompactJsonArray implements AttributeConverter<CompactJsonArray, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(CompactJsonArray obj) {
        return obj == null ? null : CompactByteArrayComposer.marshalAsArray(StaticMeta.OUTER_BONAPORTABLE_FOR_ARRAY, obj.getData(), false);
    }

    @Override
    public CompactJsonArray convertToEntityAttribute(byte[] data) {
        final List<Object> l = CompactByteArrayParser.unmarshalArray(data, StaticMeta.OUTER_BONAPORTABLE_FOR_ARRAY);
        return l == null ? null : new CompactJsonArray(l);
    }
}
