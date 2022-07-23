package de.jpaw.bonaparte.jpa.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.util.ByteArray;

@Converter(autoApply = true)
public class ConverterByteArray implements AttributeConverter<ByteArray, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(ByteArray data) {
        if (data == null)
            return null;
        return data.getBytes();
    }

    @Override
    public ByteArray convertToEntityAttribute(byte[] rawData) {
        if (rawData == null)
            return null;
        if (rawData.length == 0)
            return ByteArray.ZERO_BYTE_ARRAY;  // share instances
        return new ByteArray(rawData);
    }
}
