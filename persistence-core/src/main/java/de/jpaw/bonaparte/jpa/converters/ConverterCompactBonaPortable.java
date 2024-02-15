package de.jpaw.bonaparte.jpa.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.StaticMeta;

// persists a serialized object in a BLOB / bytea
@Converter(autoApply = true)
public class ConverterCompactBonaPortable implements AttributeConverter<BonaPortable, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(BonaPortable obj) {
        return CompactByteArrayComposer.marshal(StaticMeta.OUTER_BONAPORTABLE, obj, false);
    }

    @Override
    public BonaPortable convertToEntityAttribute(byte[] data) {
        return CompactByteArrayParser.unmarshal(data, StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
    }
}
