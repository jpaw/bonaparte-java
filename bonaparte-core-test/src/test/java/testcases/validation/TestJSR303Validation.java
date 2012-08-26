package testcases.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.initializers.FillOtherTypes;
import de.jpaw.bonaparte.pojos.tests1.OtherTypes;

public class TestJSR303Validation {
	private static Validator	validator;
	 
	@BeforeClass
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

}
