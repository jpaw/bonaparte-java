package de.jpaw.algebra;

/** Defines the properties of a Module (generalized vector space).
 * see http://en.wikipedia.org/wiki/Module_%28mathematics%29
 * (V,+) is an Abelian group, S provides the scalars.
 * The interface is implemented by V.
 *  */
public interface Module<S,V> extends AbelianGroup<V> {
    S getIdentity();        // this is really a static method and should not require an object instance. Is that possible in Java?!
    
}
