package de.jpaw.bonaparte.enums;

import java.io.Serializable;
import java.util.Set;

import de.jpaw.bonaparte.core.BonaMeta;
import de.jpaw.enums.TokenizableEnum;

public interface BonaStringEnumSet<E extends TokenizableEnum> extends BonaMeta, Set<E>, Serializable {

    public int getMaxOrdinal();
    public String getBitmap();
}
