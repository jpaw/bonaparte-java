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

import de.jpaw.bonaparte.coretests.initializers.FillBoxedTypeArrays;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;

/**
 * The TestPrimitives class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of arrays of all supported boxed primitives.
 */

public class TestBoxedTypeArrays {

    @Test
    public void testBoxedTypeArrays() throws Exception {
        SimpleTestRunner.run(FillBoxedTypeArrays.test1(), false);
    }
}
