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

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.coretests.initializers.FillOtherTypes;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.tests1.OtherTypes2;

/**
 * The TestPrimitives class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of some other special types. Using small arrays.
 */

public class TestOtherTypesS {
    @Test
    public void testOtherTypes() throws Exception {
        SimpleTestRunner.run(FillOtherTypes.test1(), false);
    }
    @Test
    public void testOtherTypes2() throws Exception {
        final OtherTypes2 ot2 = new OtherTypes2();
        ot2.setAscii1("Hello, world!");
        ot2.setUnicode1("Hällo Wörld!\r\n");
        SimpleTestRunner.run(ot2, false);
    }
}
