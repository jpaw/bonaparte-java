 /*
  * Copyright 2015 Michael Bischoff
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
package de.jpaw.bonaparte.jpa;

/**
 * Just a special case of BonaKey for the primitive long, required because Java generics don't work with primitives.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaKeyLong {

    /** Gets the class of the underlying key class for the data object.
     *
     * @return the class type of the underlying key class, never null.
     */
    public Class<?> ret$KeyClass();
    /** Gets a new message object initialized with the Entity classes data.
     *
     * @return a new object of the base class initialized with the data of this instance.
     */
    public long ret$Key();
    public void put$Key(long _d);

}
