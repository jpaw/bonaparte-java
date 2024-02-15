package de.jpaw.bonaparte.jpa;

import de.jpaw.bonaparte.core.BonaPortable;

/**
 * Just a special case of BonaPersistable for the primitive long, required because Java generics don't work with primitives.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaPersistableLong<D extends BonaPortable, T extends BonaPortable> extends BonaPersistableNoDataLong<T>, BonaPersistableData<D> {}
