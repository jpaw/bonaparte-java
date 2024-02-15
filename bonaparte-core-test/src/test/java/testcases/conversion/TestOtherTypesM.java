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

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.coretests.initializers.FillOtherTypes;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.tests1.TestBigInteger;

/**
 * The TestPrimitives class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of some other special types. Using medium sized arrays.
 */

public class TestOtherTypesM {
    @Test
    public void testOtherTypes() throws Exception {
        SimpleTestRunner.run(FillOtherTypes.test2(555), false);
    }

    @Test
    public void testBigIntegerSmall() throws Exception {
        SimpleTestRunner.run(new TestBigInteger(new BigInteger("42")), false);
    }
    @Test
    public void testBigIntegerNegative() throws Exception {
        SimpleTestRunner.run(new TestBigInteger(new BigInteger("-892743923749242")), false);
    }
    @Test
    public void testBigIntegerMedium() throws Exception {
        SimpleTestRunner.run(new TestBigInteger(new BigInteger("142426143")), false);
    }
    @Test
    public void testBigIntegerBig() throws Exception {
        SimpleTestRunner.run(new TestBigInteger(new BigInteger("427862438723648723468234682346823")), false);
    }
    @Test
    public void testBigIntegerZero() throws Exception {
        SimpleTestRunner.run(new TestBigInteger(new BigInteger("0")), false);
    }
    @Test
    public void testBigIntegerNull() throws Exception {
        SimpleTestRunner.run(new TestBigInteger(), false);
    }
}
