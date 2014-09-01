package testcases;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;

public class ValidationTest {
    
    @Test
    public void testValidationException() throws Exception {
        BasicNumericElementaryDataItem data = new BasicNumericElementaryDataItem();
        
        try {
            data.validate();
            throw new Exception("Expected an ObjectValidationException");
        } catch (ObjectValidationException e) {
            System.out.println("Got expected exception " + e);
            System.out.println("Message is " + e.getMessage());
            System.out.println("StandardDescription is " + e.getStandardDescription());
        }
    }
}
