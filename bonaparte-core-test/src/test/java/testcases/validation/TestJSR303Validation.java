package testcases.validation;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.coretests.initializers.FillOtherTypes;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTest;
import de.jpaw.bonaparte.pojos.tests1.OtherTypes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class TestJSR303Validation {
    private static Validator    validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testOtherTypes() throws Exception {
        Set<ConstraintViolation<OtherTypes>> violations;

        OtherTypes x = FillOtherTypes.test1();
        violations = validator.validate(x);
        assert violations.isEmpty() : "There should be no violations with the default data";

        x.setLanguageCode(null);
        violations = validator.validate(x);
        assert violations.isEmpty() : "There should be no violations without a language code";

        x.setCountryCode(null);
        violations = validator.validate(x);
        assert violations.size() == 1: "Without a country code we should get a problem";
        /*
        @SuppressWarnings("unchecked")
        ConstraintViolation<OtherTypes>[] v1 = (ConstraintViolation<OtherTypes>[])violations.toArray();
        ConstraintViolation<OtherTypes> v = v1[0];
        System.out.println("Problem was " + v.getMessage()); */
    }

    @Test
    public void testBeanValidationTooFewDigits() throws Exception {
        Set<ConstraintViolation<BDTest>> violations;
        BDTest x = new BDTest(new BigDecimal("3.1"));
        violations = validator.validate(x);
        assert violations.isEmpty() : "There should be no violations without a language code";

    }

}
