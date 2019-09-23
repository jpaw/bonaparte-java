package de.jpaw.bonaparte8.adapters.datetime;

import org.joda.time.Instant;

/** Converts between Java 8 Instant and Joda Instant, truncating to seconds. */
public class InstantAdapterSecond {

    public static Instant marshal(java.time.Instant obj) {
        long epochMillis = obj.toEpochMilli();
        return new Instant(epochMillis - epochMillis % 1000L);
    }

    public static java.time.Instant unmarshal(Instant data) {
        return data == null ? null : java.time.Instant.ofEpochSecond(data.getMillis() / 1000L);
    }
}
