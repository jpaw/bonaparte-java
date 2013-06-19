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

import de.jpaw.bonaparte.coretests.initializers.FillBoxedTypes;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;

/**
 * The TestBoxedTypes class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of all supported Java boxed types.
 */

public class TestBoxedTypes {
    @Test
    public void testBoxedTypes() throws Exception {
        SimpleTestRunner.run(FillBoxedTypes.test1(), false);
    }
}
