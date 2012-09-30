package testcases.validation;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.coretests.initializers.FillOtherTypes;
import de.jpaw.bonaparte.pojos.tests1.OtherTypes;

public class TestValidation {
    @Test
    public void testOtherTypes() throws Exception {
        OtherTypes x = FillOtherTypes.test1();
        
        x.validate();  // should be OK
        x.setLanguageCode(null);
        x.validate();  // should be OK
        x.setCountryCode(null);
        try {
            x.validate();
        } catch (ObjectValidationException e) {
            if (e.getErrorCode() == ObjectValidationException.MAY_NOT_BE_BLANK)
                ; // OK, expected
            else
                throw e;
        }
    }   

}
