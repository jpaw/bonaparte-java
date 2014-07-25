package de.jpaw.enums;
/** Interface of methods implemented by any XEnum class.
 * An XEnum class is essentially a beast which tries to be as similar to a standard Java enum, with the exception that
 * 1) inheritance works, i.e. additional name / value pairs can be added (Java standard enums are final)
 * 2) An enum also has a so called token, of a specified maximum length.
 * 3) CompareTo allows comparing objects if they have the same base class.
 * 4) Only a single class may extend an XEnum class. If multiple classes try to extend a base class, an exception is thrown,
 *    and possible future compareTo() invocations also all return an exception (in case the initial exception is caught).
 * 
 * There is an additional class XEnumFactory, which holds 1 instance per XEnum base class.
 * It can be queried to find the project specific instance.
 * 
 * All implementations of XEnum must inherit from the abstract base class AbstractXEnumBase.
 * 
 * The ordinal() values of an extended type start at the ordinal values of the base type.
 *
 */
public interface XEnum<E extends AbstractXEnumBase<E>> extends TokenizableEnum, Comparable<E> {
    
    Enum<?> getBaseEnum();                      // get the underlying base enum instance
    XEnumFactory<E> getFactory();               // get the factory class which created the instance
    Class<? extends E> getDeclaringClass();     // get the root XEnum class

}
