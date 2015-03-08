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

package testcases.xenum;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.testXenum.Color;
import de.jpaw.bonaparte.pojos.testXenum.MoreColors;
import de.jpaw.bonaparte.pojos.testXenum.SimpleSampleUsingEnum;
import de.jpaw.bonaparte.pojos.testXenum.SimpleSampleUsingInheritedXEnum;
import de.jpaw.bonaparte.pojos.testXenum.SimpleSampleUsingXEnum;

/**
 * The TestXEnumCompatibility class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          These are simple testcases to showcase and ensure compatibility of classes which use enums and xenums, related to equals and hashCode.
 */

public class TestXEnumCompatibility {

    @Test
    public void testEqualsAndHash() throws Exception {
        @SuppressWarnings("unused")
        Class<?> loadIt = MoreColors.class;  // ensure the inherited xenum class is loaded

        SimpleSampleUsingEnum s1 = new SimpleSampleUsingEnum();
        SimpleSampleUsingXEnum s2 = new SimpleSampleUsingXEnum();
        SimpleSampleUsingInheritedXEnum s3 = new SimpleSampleUsingInheritedXEnum();

        // assignments
        s1.setMyColor(Color.GREEN);
        s2.setMyColor(Color.GREEN);
        s3.setMyColor(Color.GREEN);

        // check hash code
        assert(s1.getMyColor().hashCode() == s2.getMyColor().hashCode());
        assert(s1.getMyColor().hashCode() == s3.getMyColor().hashCode());
        // check equals
        assert(!s1.getMyColor().equals(s2.getMyColor()));   // cannot compare downwards as equals is final for Enum :-(. This breaks the symmetry requirement for equals
        assert(s2.getMyColor().equals(s1.getMyColor()));    // same way other way around
        assert(!s1.getMyColor().equals(s3.getMyColor()));   // cannot compare downwards as equals is final for Enum :-(
        assert(s3.getMyColor().equals(s1.getMyColor()));    // same way other way around
    }
}
