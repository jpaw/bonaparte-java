package de.jpaw.bonaparte.enums;

import java.io.Serializable;
import java.util.Set;

import de.jpaw.bonaparte.core.BonaMeta;

public interface BonaEnumSet<E extends Enum<E>> extends BonaMeta, Set<E>, Serializable {

    public int getMaxOrdinal();
    public int getBitmap();
}
