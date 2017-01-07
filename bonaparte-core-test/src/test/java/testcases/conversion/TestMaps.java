package testcases.conversion;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.coretests.initializers.FillMaps;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.mapTests.AlphaColor;
import de.jpaw.bonaparte.pojos.mapTests.ClassWithEnum;
import de.jpaw.bonaparte.pojos.mapTests.ClassWithMapWithObjectWithEnum;
import de.jpaw.bonaparte.pojos.mapTests.Color;
import de.jpaw.bonaparte.testrunner.CompactTest;
import de.jpaw.bonaparte.testrunner.ExpectedTestException;
import de.jpaw.bonaparte.util.impl.RecordMarshallerCompactBonaparte;
import de.jpaw.bonaparte.util.impl.RecordMarshallerCompactBonaparteIdentity;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;
import de.jpaw.util.ExceptionUtil;

/**
 * The TestMaps class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of all supported BonaPortable Map types.
 */
public class TestMaps {

    @Test
    public void testMaps() throws Exception {
        SimpleTestRunner.run(FillMaps.test1(), false);
    }
    
    private BonaPortable generateData() {
        ClassWithEnum content = new ClassWithEnum();
        content.setColor(Color.GREEN);
        content.setAlphaColor(AlphaColor.GREEN);
        
        ClassWithMapWithObjectWithEnum mainClass = new ClassWithMapWithObjectWithEnum();
        Map<String, Object> someData = new HashMap<String, Object>();
        someData.put("myKey", content);
        mainClass.setData(someData);
        
        return mainClass;
    }
    
    @Test(expectedExceptions = AssertionError.class)
    public void testMapsWithEnums1() throws Exception {
        
        BonaPortable mainClass = generateData();
        // this test fails because the BonaPortable inside a map is not converted back into an object, it stays a map
        SimpleTestRunner.run(mainClass, false);
    }
        
    @Test(expectedExceptions = AssertionError.class)
    public void testMapsWithEnums2() throws Exception {
            
        BonaPortable mainClass = generateData();
        // this test fails because the BonaPortable inside a map is not converted back into an object, it stays a map
        CompactTest.run(mainClass, false);
    }

    @Test(expectedExceptions = ExpectedTestException.class)
    public void testMapsWithEnums3() throws Exception {
            
        BonaPortable mainClass = generateData();
        // test again, using a predefined marshaller
        RecordMarshallerCompactBonaparte marshaller = new RecordMarshallerCompactBonaparte();
        ByteArray data = marshaller.marshal(mainClass);
        
        ByteBuilder buffer = new ByteBuilder();
        buffer.write(data.getBytes());
        System.out.println("Data size is " + buffer.length());
        
        BonaPortable out = marshaller.unmarshal(buffer);
        try {
            Assert.assertEquals(out, mainClass, "(Expect difference!): ");
        } catch (AssertionError e) {
            System.out.println(ExceptionUtil.causeChain(e));
            throw new ExpectedTestException();
        }
    }

    @Test
    public void testMapsWithEnums4() throws Exception {
            
        BonaPortable mainClass = generateData();
        // test again, using a predefined marshaller
        RecordMarshallerCompactBonaparteIdentity marshaller = new RecordMarshallerCompactBonaparteIdentity();
        ByteArray data = marshaller.marshal(mainClass);
        
        ByteBuilder buffer = new ByteBuilder();
        buffer.write(data.getBytes());
        System.out.println("Data size is " + buffer.length());
        
        BonaPortable out = marshaller.unmarshal(buffer);
        Assert.assertEquals(out, mainClass);
    }
}
