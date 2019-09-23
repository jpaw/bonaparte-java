package de.jpaw.bonaparte8.adapters.datetime;

import java.time.ZoneOffset;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

/** Converts between Java 8 LocalDateTime and Joda LocalDateTime, truncating to seconds. */
public class LocalDateTimeAdapterSecond {

    public static LocalDateTime marshal(java.time.LocalDateTime obj) {
        return new LocalDateTime(obj.toEpochSecond(ZoneOffset.UTC) * 1000, DateTimeZone.UTC);
    }

    public static java.time.LocalDateTime unmarshal(LocalDateTime data) {
        if (data == null)
            return null;
        // this is ugly:
        int millisOfDay = data.getMillisOfDay();
        int secondsOfDay = millisOfDay / 1000;
        int hour = secondsOfDay / 3600;
        int minute = (secondsOfDay - 3600 * hour) / 60;
        int second = secondsOfDay % 60;
        int day = data.getDayOfMonth();
        int month = data.getMonthOfYear();
        int year = data.getYear();
        return java.time.LocalDateTime.of(year, month, day, hour, minute, second);
    }
}
