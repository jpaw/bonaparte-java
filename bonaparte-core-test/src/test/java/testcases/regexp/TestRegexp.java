package testcases.regexp;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.pojos.testRegexp.RegexpTest;
import de.jpaw.bonaparte.pojos.testRegexp.RegexpTest2;
import de.jpaw.bonaparte.pojos.testRegexp.RegexpTest3;
import de.jpaw.bonaparte.pojos.testRegexp.RegexpTest4;
import de.jpaw.util.ApplicationException;

/**
 * Test regexp with a slash.
 *
 */
public class TestRegexp {

    private void shouldFailWith(int errno, String pattern, int testClass) throws Exception {
        try {
            switch (testClass) {
            case 1:
                new RegexpTest(pattern).validate();
                break;
            case 2:
                new RegexpTest2(pattern).validate();
                break;
            case 3:
                new RegexpTest3(pattern).validate();
                break;
            case 4:
                new RegexpTest4(pattern).validate();
                break;
            }
        } catch (ApplicationException e) {
            if (e.getErrorCode() == errno)
                return;  // this is the one we wanted
            throw new Exception("Testcase " + pattern + " threw exception " + e.getErrorCode() + ", but expected " + errno);
        }
        throw new Exception("Testcase " + pattern + " threw no exception, but expected " + errno);
    }
    
    
    @Test
    public void testRegexp() throws Exception {
        new RegexpTest("069/22223456").validate();
        new RegexpTest("+35387/12345").validate();
        shouldFailWith(ObjectValidationException.NO_PATTERN_MATCH, "nonono", 1);
    }
    
    @Test
    public void testRegexp2() throws Exception {
        new RegexpTest2("069/22223456").validate();
        new RegexpTest2("+35387/12345").validate();
        shouldFailWith(ObjectValidationException.NO_PATTERN_MATCH, "nonono", 2);
    }
    
    @Test   // with space
    public void testRegexp3() throws Exception {
        new RegexpTest3("069/22223456").validate();
        new RegexpTest3("+35387/12345").validate();
        shouldFailWith(ObjectValidationException.NO_PATTERN_MATCH, "nonono", 3);
    }
    
    @Test  // with typedef
    public void testRegexp4() throws Exception {
        new RegexpTest4("069/22223456").validate();
        new RegexpTest4("+35387/12345").validate();
        shouldFailWith(ObjectValidationException.NO_PATTERN_MATCH, "nonono", 4);
    }
}
