package de.jpaw.bonaparte.benchmarks.propsVsAnnos;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface BenchAnnoClass {
    public String value();
}
