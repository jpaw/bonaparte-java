package de.jpaw.bonaparte.core.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;

public class ValidationTest {

    @Test
    public void testValidationException() throws Exception {
        BasicNumericElementaryDataItem data = new BasicNumericElementaryDataItem();

        final ObjectValidationException e = Assertions.assertThrows(ObjectValidationException.class, () -> {
            data.validate();
        });
        System.out.println("Got expected exception " + e);
        System.out.println("Message is " + e.getMessage());
        System.out.println("StandardDescription is " + e.getStandardDescription());
    }
}
