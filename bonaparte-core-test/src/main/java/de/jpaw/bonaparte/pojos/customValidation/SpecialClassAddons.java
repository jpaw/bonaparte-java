package de.jpaw.bonaparte.pojos.customValidation;

import de.jpaw.bonaparte.core.ObjectValidationException;

public class SpecialClassAddons {

    public static void preprocess(SpecialClass obj) {
    }
    
    public static void validate(SpecialClass obj) throws ObjectValidationException {
        
        // special check: lower bound cannot exceed upper bound
        if (obj.lower > obj.upper)
            throw new ObjectValidationException(ObjectValidationException.CUSTOM_VALIDATION);
    }

}
