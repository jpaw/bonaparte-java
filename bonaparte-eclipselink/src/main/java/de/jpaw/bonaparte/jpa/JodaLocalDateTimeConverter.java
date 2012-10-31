package de.jpaw.bonaparte.jpa;

import java.util.Calendar;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.LocalDateTime;

import de.jpaw.util.DayTime;

public class JodaLocalDateTimeConverter implements Converter {

    private static final long serialVersionUID = 1L;

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        return dataValue == null ? null : LocalDateTime.fromCalendarFields((Calendar) dataValue);
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue == null ? null : DayTime.toGregorianCalendar((LocalDateTime) objectValue);
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
    }

    @Override
    public boolean isMutable() {
        return false;
    }

}