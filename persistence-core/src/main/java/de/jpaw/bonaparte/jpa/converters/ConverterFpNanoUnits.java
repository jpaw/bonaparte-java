package de.jpaw.bonaparte.jpa.converters;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.fixedpoint.types.NanoUnits;

@Converter(autoApply = true)
public class ConverterFpNanoUnits implements AttributeConverter<NanoUnits, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(NanoUnits data) {
        if (data == null)
            return null;
        return data.toBigDecimal();
    }

    @Override
    public NanoUnits convertToEntityAttribute(BigDecimal rawData) {
        if (rawData == null)
            return null;
        if (rawData.signum() == 0)
            return NanoUnits.ZERO;
        if (rawData.compareTo(BigDecimal.ONE) == 0)
            return NanoUnits.ONE;
        return NanoUnits.valueOf(rawData);
    }
}
