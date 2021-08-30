package de.jpaw.bonaparte.coretests.initializers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import de.jpaw.bonaparte.pojos.tests1.Instants;

public class FillInstants {

    public static Instants fillInstants() {
        Instants s = new Instants();
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        s.setI3(now);
        now = now.truncatedTo(ChronoUnit.SECONDS);
        s.setI1(now);
        s.setI2(now);
        return s;
    }
}
