package de.jpaw.bonaparte.jpa;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target(TYPE)
@Retention(RUNTIME)
public @interface KeyClass {
    public Class<? extends Serializable> value();
}
