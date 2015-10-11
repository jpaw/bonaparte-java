 /*
  * Copyright 2012 Michael Bischoff
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package testcases.conversion;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.coretests.initializers.FillPrimitives;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.tests1.Longtest;
import de.jpaw.bonaparte.pojos.tests1.Primitives;
import de.jpaw.util.ByteBuilder;
import de.jpaw.util.ByteUtil;

/**
 * The TestPrimitives class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of all supported Java primitives.
 */

public class TestPrimitives {

    @Test
    public void testPrimitives() throws Exception {
        SimpleTestRunner.run(FillPrimitives.test1(), false);
    }

    @Test
    public void testPrimitiveLongFibonacci() throws Exception {
        final int n = 91;
        // run the serialization and deserialization for various numeric magnitudes
        long [] fibonacci = new long [n];

        fibonacci[0] = 1;
        fibonacci[1] = 1;
        for (int i = 2; i < n; ++i) {
            fibonacci[i] = fibonacci[i-1] + fibonacci[i-2];
            if (fibonacci[i] < 0)
                System.out.println("Fibonacci[" + i + "] is negative");
        }
        for (int i = 2; i < n; ++i) {
            Longtest obj = new Longtest(fibonacci[i]);
            SimpleTestRunner.run(obj, false);
        }
    }
    
    @Test
    public void testPrimitiveIntegralsFibonacci() throws Exception {
        final int n = 91;
        // run the serialization and deserialization for various numeric magnitudes
        long [] fibonacci = new long [n];

        fibonacci[0] = 1;
        fibonacci[1] = 1;
        for (int i = 2; i < n; ++i) {
            fibonacci[i] = fibonacci[i-1] + fibonacci[i-2];
            if (fibonacci[i] < 0)
                System.out.println("Fibonacci[" + i + "] is negative");
        }
        
        ByteBuilder bb = new ByteBuilder();
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(bb, false);
        Primitives p = new Primitives();
        for (int i = 0; i < n; ++i) {
            p.setBoolean1(false);
            p.setByte1((byte)fibonacci[i]);
            p.setShort1((short)fibonacci[i]);
            p.setInt1((int)fibonacci[i]);
            p.setLong1(fibonacci[i]);
            p.setChar1((char)fibonacci[i]);
            cbac.addField(Primitives.meta$$this, p);
        }
        byte [] result = cbac.getBytes();
        int hash = Arrays.hashCode(result);
        System.out.println("Length is " + result.length + ", hash code of result is " + hash);
        Assert.assertEquals(result.length, 198);
        Assert.assertEquals(hash, -509876399);
    }

    @Test
    public void testStrings() throws Exception {
        String [] tests = {
            "Z", "hello", "hello world with more than 16",          // 1, 6, 29 chars (+1) (+2)
            "ü", "grün",  "gräßlich und auch sehr lang",            // 1, 5, 27 chars (+2), 27 chars (+4) => all stored as UTF-8 with 2 byte prefix
            "€", "€€",    "jksdfksdfh€lsdfjsdlfj sdlfj sldfj jsld " // 1, 2, 39 chars (+2) (+4) (+41) => all stored as UTF-16 with 1/2/2 byte prefix (first is a char)
        };
        
        ByteBuilder bb = new ByteBuilder();
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(bb, false);
        for (int i = 0; i < tests.length; ++i) {
            cbac.addField((AlphanumericElementaryDataItem)null, tests[i]);
        }
        byte [] result = cbac.getBytes();
        int hash = Arrays.hashCode(result);
        System.out.println("Length is " + result.length + ", hash code of result is " + hash);
        Assert.assertEquals(result.length, 164);
        Assert.assertEquals(hash, -299523436);
    }

    @Test
    public void testStrings2() throws Exception {
        String [] tests = {
            "Z", "hello", "hello world with more than 16",          // 1, 6, 29 chars (+1) (+2)
            "ü", "grün",  "gräßlich und auch sehr lang",            // 1, 5, 27 chars (+2) (+1) (+2) => using ISO
            "€", "€€",    "jksdfksdfh€lsdfjsdlfj sdlfj sldfj jsld ", // 1, 2, 39 chars (+2) (+4) (+41) => all stored as UTF-16 with 1/2/2 byte prefix (first is a char)
            "\u03B1\u03B2\u03B3 ..."                                // UTF8: UTF-String + length byte + 10 bytes for the string
        };
        int lengths [] = {
            1, 6, 31,
            3, 5, 29,
            3, 6, 80,
            12
        };
        int hashes [] = {
            121, -1189149473, -899288326,
            -10575, -39303953, -967932771,
            -9663, 804336766, -785021038,
            2125008742
        };
        ByteBuilder bb = new ByteBuilder();
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(bb, false);
        for (int i = 0; i < tests.length; ++i) {
            cbac.reset();
            cbac.addField((AlphanumericElementaryDataItem)null, tests[i]);
            byte [] result = cbac.getBytes();
            int hash = Arrays.hashCode(result);
            System.out.println("Length is " + result.length + ", hash code of result is " + hash);
            Assert.assertEquals(result.length, lengths[i], "length for run " + i);
            Assert.assertEquals(hash, hashes[i], "hash for run " + i);
        }
    }
    
    @Test
    public void testStrings1() throws Exception {
        ByteBuilder bb = new ByteBuilder();
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(bb, false);
        cbac.addField((AlphanumericElementaryDataItem)null, "Xü");
        System.out.println(String.format("Chars are 0x%04x 0x%04x", (int)'X', (int)'ü'));
        System.out.println(ByteUtil.dump(cbac.getBytes(), 100));
    }
    
    @Test
    public void testStrings1a() throws Exception {
        ByteBuilder bb = new ByteBuilder();
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(bb, false);
        cbac.addField((AlphanumericElementaryDataItem)null, "grün");
        System.out.println(String.format("Chars are 0x%04x 0x%04x", (int)'X', (int)'ü'));
        System.out.println(ByteUtil.dump(cbac.getBytes(), 100));
    }
    
    @Test
    public void testStrings1Greek() throws Exception {
        ByteBuilder bb = new ByteBuilder();
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(bb, false);
        String test = "\u03B1\u03B2\u03B3 ...";
        cbac.addField((AlphanumericElementaryDataItem)null, test);  // 7 characters, 7+3 = 10 bytes in UTF-8
        System.out.println(String.format("String length is %d for %s", test.length(), test));
        System.out.println(ByteUtil.dump(cbac.getBytes(), 100));
    }
}
