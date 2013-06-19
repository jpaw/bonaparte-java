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

import de.jpaw.bonaparte.pojos.meta.ClassDefinition;

/**
 * This interface adds a method to retrieve full object Metadata.
 * The class and its implementation is usually created by the bonaparte DSL.
 * It is a separate interface, because most of the composers / parsers do not
 * need the meta information, and therefore if some classes must be created
 * manually, you do not need to worry about filling the "ClassDefinition" data structure,
 * unless you plan to use the automated class builder functionality.
 * You need this interface in case you want to do marshalling and not just serialization.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaPortableWithMetaData extends BonaPortable {
    public ClassDefinition get$MetaData();  // name, revision etc as a class object. Use $ to avoid conflict with other getters
}
