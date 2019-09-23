package de.jpaw.bonaparte8.adapters.datetime.test;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

import de.jpaw.bonaparte8.adapters.datetime.InstantAdapterMilliSec;
import de.jpaw.bonaparte8.adapters.datetime.InstantAdapterSecond;
import de.jpaw.bonaparte8.adapters.datetime.LocalDateAdapter;
import de.jpaw.bonaparte8.adapters.datetime.LocalDateTimeAdapterMilliSec;
import de.jpaw.bonaparte8.adapters.datetime.LocalDateTimeAdapterSecond;
import de.jpaw.bonaparte8.adapters.datetime.LocalTimeAdapterMilliSec;
import de.jpaw.bonaparte8.adapters.datetime.LocalTimeAdapterSecond;

public class ConvertersTest {
    // since the precision of Joda is one millisecond, we can use now() to create random instances and expect that their value
    // is identical after a roundtrip to Java 8 date / time and back. Please note that the opposite will not be true, as sub-millisecond
    // portions will be cut off.

    @Test
    public void testJava8ConversionInstant() throws Exception {

        Instant currentInstant = new Instant();
        Instant newInstant = InstantAdapterMilliSec.marshal(InstantAdapterMilliSec.unmarshal(currentInstant));

        Assert.assertTrue(currentInstant.equals(newInstant));

        // now do the same with possible truncation
        newInstant = InstantAdapterSecond.marshal(InstantAdapterSecond.unmarshal(currentInstant));

        // evaluation of the result
        long originalMillis = currentInstant.getMillis();
        long truncatedMillis = newInstant.getMillis();

        Assert.assertEquals(0, truncatedMillis % 1000);
        Assert.assertTrue(truncatedMillis <= originalMillis);
        Assert.assertTrue((truncatedMillis + 999) >= originalMillis);
    }

    @Test
    public void testJava8ConversionLocalDateTime() throws Exception {

        LocalDateTime currentLocalDateTime = new LocalDateTime();
        LocalDateTime newLocalDateTime = LocalDateTimeAdapterMilliSec.marshal(LocalDateTimeAdapterMilliSec.unmarshal(currentLocalDateTime));

        Assert.assertTrue(currentLocalDateTime.equals(newLocalDateTime));

        // now do the same with possible truncation
        newLocalDateTime = LocalDateTimeAdapterSecond.marshal(LocalDateTimeAdapterSecond.unmarshal(currentLocalDateTime));

        // evaluation of the result
        long originalMillis = currentLocalDateTime.getMillisOfDay();
        long truncatedMillis = newLocalDateTime.getMillisOfDay();

        Assert.assertEquals(0, truncatedMillis % 1000);
        Assert.assertTrue(truncatedMillis <= originalMillis);
        Assert.assertTrue((truncatedMillis + 999) >= originalMillis);
    }

    @Test
    public void testJava8ConversionLocalTime() throws Exception {

        LocalTime currentLocalTime = new LocalTime();
        LocalTime newLocalTime = LocalTimeAdapterMilliSec.marshal(LocalTimeAdapterMilliSec.unmarshal(currentLocalTime));

        Assert.assertTrue(currentLocalTime.equals(newLocalTime));

        // now do the same with possible truncation
        newLocalTime = LocalTimeAdapterSecond.marshal(LocalTimeAdapterSecond.unmarshal(currentLocalTime));

        // evaluation of the result
        long originalMillis = currentLocalTime.getMillisOfDay();
        long truncatedMillis = newLocalTime.getMillisOfDay();

        Assert.assertEquals(0, truncatedMillis % 1000);
        Assert.assertTrue(truncatedMillis <= originalMillis);
        Assert.assertTrue((truncatedMillis + 999) >= originalMillis);
    }

    @Test
    public void testJava8ConversionDay() throws Exception {

        LocalDate someDay = new LocalDate(1958, 7, 30);
        LocalDate newDay = LocalDateAdapter.marshal(LocalDateAdapter.unmarshal(someDay));

        Assert.assertTrue(someDay.equals(newDay));
    }
}
