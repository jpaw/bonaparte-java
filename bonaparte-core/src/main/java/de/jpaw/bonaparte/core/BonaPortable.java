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

import java.io.Serializable;

/** 
 * This interface defines the methods any object which should be serialized into the bonaparte format must implement.
 * The class and its implementation is usually created by the bonaparte DSL. 
 * @author Michael Bischoff
 *
 **/
public interface BonaPortable extends Serializable {
	// methods to return static class data
	public String get$PQON();      	// partially qualified object name:  embed $ to avoid conflict with other getters
	public String get$Revision();  	// use $ to avoid name clash / conflict with other getters
    public String get$Parent();		// get the parent class (also possible via getClass() )
    public String get$Bundle();		// get the bundle this class / package is supposed to sit in
    
    // public void serialize(MessageComposer w);	// not required any more, only serializeSub is called by the composers
    public <E extends Exception> void serializeSub(MessageComposer<E> w) throws E;             // serialize the fields of an object
    public <E extends Exception> void deserialize(MessageParser<E> p) throws E;
    public boolean hasSameContentsAs(BonaPortable that);     // returns same result as equals(), but starts with a more type specific object
    public void validate() throws ObjectValidationException; // verify that data is compliant with patterns, required data is not null etc.

    // WIP and moved to a separate interface extending this one...
    // next method is not required by serialization, but for marshalling (it could be replaced by reflection methods)
	// public ClassDefinition get$MetaData();  // name, revision etc as a ClassDefinitiob object. Use $ to avoid conflict with other getters
}
