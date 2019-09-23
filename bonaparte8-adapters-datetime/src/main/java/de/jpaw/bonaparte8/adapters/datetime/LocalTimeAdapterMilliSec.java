package de.jpaw.bonaparte8.adapters.datetime;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

/** Converts between Java 8 LocalTime and Joda LocalTime, truncating to milliseconds. */
public class LocalTimeAdapterMilliSec {

    public static LocalTime marshal(java.time.LocalTime obj) {
        return new LocalTime(obj.toNanoOfDay() / 1_000_000L, DateTimeZone.UTC);
    }

    public static java.time.LocalTime unmarshal(LocalTime data) {
        return data == null ? null : java.time.LocalTime.ofNanoOfDay(data.getMillisOfDay() * 1_000_000L);
    }
}
