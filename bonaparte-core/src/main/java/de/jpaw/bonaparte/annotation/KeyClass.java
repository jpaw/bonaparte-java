package de.jpaw.bonaparte.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.jpaw.bonaparte.core.BonaPortable;

@Target(TYPE)
@Retention(RUNTIME)
public @interface KeyClass {
    public Class<? extends BonaPortable> value();
}
