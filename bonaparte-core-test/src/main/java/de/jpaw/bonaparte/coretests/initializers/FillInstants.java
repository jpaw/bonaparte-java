package de.jpaw.bonaparte.coretests.initializers;

import java.time.Instant;

import de.jpaw.bonaparte.pojos.tests1.Instants;

public class FillInstants {

    public static Instants fillInstants() {
        Instants s = new Instants();
        Instant now = Instant.now();
        s.setI3(now);
        now = now.minusNanos(now.getNano());
        s.setI1(now);
        s.setI2(now);
        return s;
    }
}
