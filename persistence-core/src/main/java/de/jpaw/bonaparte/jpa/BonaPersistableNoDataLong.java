package de.jpaw.bonaparte.jpa;

import de.jpaw.bonaparte.core.BonaPortable;

/**
 * Just a special case of BonaPersistableNoData for the primitive long, required because Java generics don't work with primitives.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaPersistableNoDataLong<T extends BonaPortable> extends BonaPersistableTracking<T>, BonaPersistableKeyLong {}
