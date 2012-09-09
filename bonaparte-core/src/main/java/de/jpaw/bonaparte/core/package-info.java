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
/**
 * The BONAPARTE core library provides the <code>BonaPortable</code> interface, which must be implemented by every class to be serializable with the bonaparte format, as well as interfaces and implementations for the serializers and deserializers.
 * 
 * An extended interface <code>BonaPortableWithMetaData</code> offers a method to return a description of the object itself in classes implementing the <code>BonaPortable</code> interface. (This is still work in progress.)
 * <p>
 * Currently, two different serialization formats are provided, one which is a binary format which replaces the standard Java serialization, and the bonaparte format, which is mainly text based and can be regarded as a generalized CSV format.
 * 
 * @author Michael Bischoff
 * 
 */
package de.jpaw.bonaparte.core;