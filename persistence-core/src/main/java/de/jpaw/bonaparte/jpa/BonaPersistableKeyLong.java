package de.jpaw.bonaparte.jpa;

/**
 * Just a special case of BonaPersistableKey for the primitive long, required because Java generics don't work with primitives.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaPersistableKeyLong extends BonaKeyLong, BonaPersistableBase {}
