package de.jpaw.bonaparte.core;

/** Interfaces which defines a method which must be implemented by custom validators for BonaPortable objects.
 * The implementing class must be in the same package as the object to be validated, with a name of (baseclass)Validator. */
public interface CustomValidator {

    /** Invoked method for validation. If the method returns true, then further validations for this object
     * should be skipped. Otherwise, they will be performed as usual.
     * 
     * It is important to know that at time of invocation, neither this class not any of its superclasses have been validated.
     * 
     * @param obj
     * @return skipDefaultValidations
     * @throws ObjectValidationException
     */
    public boolean validate(BonaPortable obj) throws ObjectValidationException;
}
