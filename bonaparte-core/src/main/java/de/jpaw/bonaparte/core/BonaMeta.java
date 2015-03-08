package de.jpaw.bonaparte.core;

import java.io.Serializable;

/**
 * Defines the common identification methods of BonaPortables, Bonaparte enums and BonaParte XEnums.
 * The class and its implementation is usually created by the bonaparte DSL.
 * Defined in the JPAW project because handwritten enums might want it as well.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaMeta extends Serializable {

    /** Gets the partially qualified object name (the fully qualified name minus some constant package prefix).
     * This is a constant string (static final), but defined as a member function in order to be able to declare it in the interface.
     *
     * @return the partially qualified object name as a not-null Java String.
     */
    public String get$PQON();

    /** Gets the partially qualified object name of this object's parent, or null if the object does not extend another object.
     * This is a constant string (static final), but defined as a member function in order to be able to declare it in the interface.
     * The expression <code>getClass().getCanonicalName()</code> applied to the object's parent consists of some package prefix concatenated with the return value of this function.
     *
     * @return the partially qualified object name of the parent class as a Java String, or null if the object has no explicit superclass.
     */
    public String get$Parent();

    /** Gets the bundle information as defined in the DSL, or null. Bundles do not yet have any functional effect, they are reserved to allow the grouping into OSGi bundles in the future.
     * Therefore, do not yet use this feature.
     * This is a constant string (static final), but defined as a member function in order to be able to declare it in the interface.
     *
     * @return the bundle as defined in the DSL as a Java String, or null, if no bundle has been defined for objects of this class.
     */
    public String get$Bundle();
}
