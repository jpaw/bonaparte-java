 /*
  * Copyright 2014 Michael Bischoff
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

import de.jpaw.bonaparte.pojos.meta.ObjectReference;

/** Defines the methods which a custom serialization / deserialization proxy must implement.
 * This can be used to write all objects of a specific class, for example, as ordinary integers.
 * Replacements for null objects will be written as a single scalar null in any case.
 * It is essential that the serialization of any non-null object must write a non-null item first.
 *
 * When objects are referenced in other objects, the base class must be specific enough to allow
 * the recognition of the replacement functionality, i.e. reading an object into a generic
 * BonaPortable won't work.
 *
 * Objects which should allow this must have the "proxyable" flag set, as the check is not made for any
 * object, due to performance considerations.
 *
 * This functionality is similar to XmlAdapter, but allows to write multiple scalar objects, instead of just a 1:1
 * type conversion. It is also similar to Java's standard serialization writeReplace/readResolve in the context,
 * that using this interface moves the responsibility of object allocation to the implementation.
 *
 * @author Michael Bischoff
 *
 */
public interface BonaProxy<D extends BonaPortable, CE extends Exception, PE extends Exception> {

    /** Write the replacement of object obj to the output channel defined by composer. */
    void writeReplace(D obj, MessageComposer<CE> composer) throws CE;

    /** Read some data from the Write the replacement of object obj to the output channel defined by composer. */
    D readResolve(ObjectReference di, MessageParser<PE> parser) throws PE;
}
