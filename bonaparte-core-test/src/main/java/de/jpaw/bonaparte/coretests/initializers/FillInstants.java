package de.jpaw.bonaparte.coretests.initializers;

import org.joda.time.Instant;

import de.jpaw.bonaparte.pojos.tests1.Instants;

public class FillInstants {

    public static Instants fillInstants() {
        Instant now = new Instant();
        Instants s = new Instants();
        s.setI3(now);

        long millis = now.getMillis();
        millis -= millis % 1000;
        now = new Instant(millis);
        s.setI1(now);
        s.setI2(now);
        return s;
    }
}
