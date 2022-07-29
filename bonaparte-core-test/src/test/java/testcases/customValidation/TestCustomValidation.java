package testcases.customValidation;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.pojos.customValidation.SpecialClass;

public class TestCustomValidation {

    @Test
    public void testCustomOK() throws Exception {
        SpecialClass s = new SpecialClass(3, 8);
        s.validate();
    }

    @Test
    public void testCustomError() throws Exception {
        SpecialClass s = new SpecialClass(8, 3);
        try {
            s.validate();
            throw new Exception("Expected a validation exception here");
        } catch (ObjectValidationException e) {
            if (e.getErrorCode() != ObjectValidationException.CUSTOM_VALIDATION)
                throw e;
        }
    }

}
