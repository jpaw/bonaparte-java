package testcases.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.pojos.bigdecimal.BDTest;

public class TestBigDecimalValidation {
    private static Validator    validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testBigDecimal() throws Exception {
        Set<ConstraintViolation<BDTest>> violations;

        BDTest bd = new BDTest(new BigDecimal("3.14"));
        System.out.println("Scale is " + bd.getAmount().scale() + ", precision is " + bd.getAmount().precision());
        violations = validator.validate(bd);
        assert violations.size() == 0: "Issue with BigDecimal";

        bd = new BDTest(new BigDecimal("3.14000000"));
        System.out.println("Scale is " + bd.getAmount().scale() + ", precision is " + bd.getAmount().precision());
        bd.setAmount(bd.getAmount().setScale(6, RoundingMode.UNNECESSARY));
        System.out.println("New Scale is " + bd.getAmount().scale() + ", precision is " + bd.getAmount().precision() + ", value is " + bd.getAmount().toString());

        violations = validator.validate(bd);
        assert violations.size() == 0: "Issue with BigDecimal";

    }

}
