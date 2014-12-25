package de.jpaw.bonaparte.enums;

import java.io.Serializable;
import java.util.Set;

import de.jpaw.bonaparte.core.BonaMeta;
import de.jpaw.enums.TokenizableEnum;

/** Interface implemented by EnumSet as well as XEnumSet for String type bitmaps. */
public interface BonaStringEnumSet<E extends TokenizableEnum> extends BonaMeta, Set<E>, Serializable {

    public int getMaxOrdinal();
    public String getBitmap();
}
