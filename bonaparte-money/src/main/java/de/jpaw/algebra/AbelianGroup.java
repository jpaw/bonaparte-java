package de.jpaw.algebra;

import de.jpaw.money.MonetaryException;

/** Defines the properties of an Abelian group type.
 * For simplicity, the operator is called plus and the neutral element ZERO.
 *  */
public interface AbelianGroup<T> {
    //T getZero();        // this is really a static method and should not require an object instance. Is that possible in Java?!
    T add(T b) throws MonetaryException;         // add two elements
    T negate() throws MonetaryException;         // return minus a (the inverse element)
    T subtract(T b) throws MonetaryException;    // shorthand for a.add(minus(b))
}
