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

import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.ParsedFoldingComponent;

/** Defines the methods which a class with embedded support for custom serialization / deserialization proxy must implement.
 *
 * @author Michael Bischoff
 *
 */
public interface BonaCustom extends BonaMeta {

    /** Gets the Metadata of the BonaPortable (which is a BonaPortable itself). */
    public ClassDefinition ret$MetaData();  // name, revision etc as a class object. Use $ to avoid conflict with other getters

    /** Serializes this object into the format implemented by the MessageComposer parameter. The method will invoke methods of the MessageComposer interface for every member field, and also for some metadata. Class headers itself are assumed to have been serialized before.
     *  Different implementations are provided with the bonaparte library, for ASCII-like formats (bonaparte) or binary formats plugging into the standard Java {@link java.io.Serializable}/{@link java.io.Externalizable} interface.
     *
     * @param w the implementation of the serializer.
     * @throws E is usually either {@link RuntimeException}, for serializers writing to in-memory buffers, where no checked exceptions are thrown, or {@link java.io.IOException}, for serializers writing to streams.
     */
    public <E extends Exception> void serializeSub(MessageComposer<E> w) throws E;

    /** Serializes this object using a mapping. Not all fields are output, and not in the sequence they are declared in the class.
     * In addition, specific indexes can be selected for arrays or Lists.
     *
     * @param w the implementation of the serializer.
     * @throws E is usually either {@link RuntimeException}, for serializers writing to in-memory buffers, where no checked exceptions are thrown, or {@link java.io.IOException}, for serializers writing to streams.
     */
    public <E extends Exception> void foldedOutput(MessageComposer<E> w, ParsedFoldingComponent pfc) throws E;
}
