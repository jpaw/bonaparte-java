package de.jpaw.bonaparte.jpa;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import de.jpaw.bonaparte.core.BonaPortable;

@Target(TYPE)
@Retention(RUNTIME)
public @interface TrackingClass {
    public Class<? extends BonaPortable> value();
}
