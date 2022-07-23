package de.jpaw.bonaparte.jpa.converters;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.fixedpoint.types.MicroUnits;

@Converter(autoApply = true)
public class ConverterFpMicroUnits implements AttributeConverter<MicroUnits, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(MicroUnits data) {
        if (data == null)
            return null;
        return data.toBigDecimal();
    }

    @Override
    public MicroUnits convertToEntityAttribute(BigDecimal rawData) {
        if (rawData == null)
            return null;
        if (rawData.signum() == 0)
            return MicroUnits.ZERO;
        if (rawData.compareTo(BigDecimal.ONE) == 0)
            return MicroUnits.ONE;
        return MicroUnits.valueOf(rawData);
    }
}
