package de.jpaw.bonaparte8.adapters.datetime;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

/** Converts between Java 8 LocalTime and Joda LocalTime, truncating to seconds. */
public class LocalTimeAdapterSecond {

    public static LocalTime marshal(java.time.LocalTime obj) {
        return new LocalTime(obj.toSecondOfDay() * 1000L, DateTimeZone.UTC);
    }

    public static java.time.LocalTime unmarshal(LocalTime data) {
        return data == null ? null : java.time.LocalTime.ofSecondOfDay(data.getMillisOfDay() / 1000);
    }
}
