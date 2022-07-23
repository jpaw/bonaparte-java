package de.jpaw.bonaparte.jpa.converters;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.fixedpoint.types.MilliUnits;

@Converter(autoApply = true)
public class ConverterFpMilliUnits implements AttributeConverter<MilliUnits, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(MilliUnits data) {
        if (data == null)
            return null;
        return data.toBigDecimal();
    }

    @Override
    public MilliUnits convertToEntityAttribute(BigDecimal rawData) {
        if (rawData == null)
            return null;
        if (rawData.signum() == 0)
            return MilliUnits.ZERO;
        if (rawData.compareTo(BigDecimal.ONE) == 0)
            return MilliUnits.ONE;
        return MilliUnits.valueOf(rawData);
    }
}
