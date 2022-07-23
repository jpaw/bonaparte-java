package de.jpaw.bonaparte.jpa;

import de.jpaw.bonaparte.core.BonaPortable;

/**
 * Defines the methods a JPA entity class implements.
 * The classes and their implementations are usually created by the bonaparte add-on DSL BDDL.
 * This is an interface using generics for the DATA type.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaPersistableData<D extends BonaPortable> extends BonaPersistableBase, BonaData<D> {
}
