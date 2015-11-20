package de.jpaw.bonaparte.core.tests;

import org.testng.annotations.Test;

public class ReflectionTypecheckTest {
    
    private void checkType(String msg, Object o) {
        System.out.println(msg + " isArray: " + o.getClass().isArray());
        System.out.println(msg + " instOf Object []: " + (o instanceof Object []));
    }
    
    @Test
    public void testReflection() throws Exception {
        checkType("byte []",      new byte [8]);
        checkType("String []",    new String [8]);
        checkType("Object []",    new Object [8]);
    }

}
