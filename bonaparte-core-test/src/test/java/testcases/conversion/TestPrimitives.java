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

import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.initializers.FillPrimitives;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.tests1.Longtest;

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

}
