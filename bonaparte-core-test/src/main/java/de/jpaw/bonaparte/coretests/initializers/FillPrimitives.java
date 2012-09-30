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

package de.jpaw.bonaparte.coretests.initializers;

import de.jpaw.bonaparte.pojos.tests1.Primitives;

public class FillPrimitives {

    static public Primitives test1() {
        Primitives x = new Primitives();
        x.setByte1((byte) 13);
        x.setShort1((short) 42);
        x.setBoolean1(true);
        x.setChar1('Ä');
        x.setChar2('\n');
        x.setDouble1(42.5d);
        x.setFloat1(5.0e17f);
        x.setInt1(4242);
        x.setInt2(42424242);
        x.setLong1(424242424242424242L);
        return x;
    }
}
