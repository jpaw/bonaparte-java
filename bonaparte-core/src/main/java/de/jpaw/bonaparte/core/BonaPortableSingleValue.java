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
package de.jpaw.bonaparte.core;

/**
 * This interface adds methods to retrieve a single value (boxed primitive or BonaPortable Object) or set it.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaPortableSingleValue<T> extends BonaPortable {
	public T getValue();   			// retrieve the value
	public void setValue(T _val);	// set the value
}
