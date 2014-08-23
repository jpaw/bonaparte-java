package de.jpaw.bonaparte.core;

/** Interface which defines methods which must be implemented by custom addon classes for BonaPortable objects.
 * The implementing class must be in the same package as the customized class, with a name of (baseclass)Addons.
 * This is a pseudo interface, as the implementing class must define the methods as static methods! */
public interface CustomAddons {

    /** Method which will be invoked before a validation. This can be used to auto-fix some issues, in case the object is known not to be frozen.
     * 
     * @param obj
     * @throws ObjectValidationException - Either the default ObjectValidationException.CUSTOM_VALIDATION should be thrown,
     * or better a specific exception defined in some class inherited from ObjectValidationException.  
     */
    public void preprocess(BonaPortable obj) throws ObjectValidationException;
    
    /** Invoked method after the default validation has been performed.
     * 
     * @param obj
     * @throws ObjectValidationException - Either the default ObjectValidationException.CUSTOM_VALIDATION should be thrown,
     * or better a specific exception defined in some class inherited from ObjectValidationException.  
     */
    public void validate(BonaPortable obj) throws ObjectValidationException;
}
