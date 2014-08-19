package de.jpaw.bonaparte.pojos.customValidation;

import de.jpaw.bonaparte.core.ObjectValidationException;

public class SpecialClassValidator {

    public static boolean validate(SpecialClass obj) throws ObjectValidationException {
        
        // special check: lower bound cannot exceed upper bound
        if (obj.lower > obj.upper)
            throw new ObjectValidationException(ObjectValidationException.CUSTOM_VALIDATION);
        return false;
    }

}
