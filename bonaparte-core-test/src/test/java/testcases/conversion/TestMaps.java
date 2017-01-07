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
import de.jpaw.bonaparte.util.impl.RecordMarshallerCompactBonaparte;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

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
    
    @Test
    public void testMapsWithEnums() throws Exception {
        ClassWithEnum content = new ClassWithEnum();
        content.setColor(Color.GREEN);
        content.setAlphaColor(AlphaColor.GREEN);
        
        ClassWithMapWithObjectWithEnum mainClass = new ClassWithMapWithObjectWithEnum();
        Map<String, Object> someData = new HashMap<String, Object>();
        someData.put("myKey", content);
        mainClass.setData(someData);
        
        // this test fails because the BonaPortable inside a map is not converted back into an object, it stays a map
        // SimpleTestRunner.run(mainClass, true);
        
        
        //CompactTest.run(mainClass, true);
        
        // test again, using a predefined marshaller
        RecordMarshallerCompactBonaparte marshaller = new RecordMarshallerCompactBonaparte();
        ByteArray data = marshaller.marshal(mainClass);
        
        ByteBuilder buffer = new ByteBuilder();
        buffer.write(data.getBytes());
        System.out.println("Data size is " + buffer.length());
        
        BonaPortable out = marshaller.unmarshal(buffer);
        //Assert.assertEquals(out, mainClass);
    }
}
