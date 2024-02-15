package de.jpaw.bonaparte.jpa.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.jpa.json.CompactJsonElement;

// persists a serialized object in a BLOB / bytea
@Converter(autoApply = true)
public class ConverterCompactJsonElement implements AttributeConverter<CompactJsonElement, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(CompactJsonElement obj) {
        return obj == null ? null : CompactByteArrayComposer.marshalAsElement(StaticMeta.OUTER_BONAPORTABLE_FOR_ELEMENT, obj.getData(), false);
    }

    @Override
    public CompactJsonElement convertToEntityAttribute(byte[] data) {
        Object l = CompactByteArrayParser.unmarshalElement(data, StaticMeta.OUTER_BONAPORTABLE_FOR_ELEMENT);
        return l == null ? null : new CompactJsonElement(l);
    }
}
