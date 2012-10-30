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
package de.jpaw.bonaparte.jpa;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.util.ApplicationException;

/** 
 * Defines the data related methods a JPA entity class implements.
 * The classes and their implementations are usually created by the bonaparte add-on DSL BDDL.
 * This is an interface using generics for the DATA type.
 *  
 * @author Michael Bischoff
 *
 **/
public interface BonaData<D extends BonaPortable> {
    
    /** Gets the partially qualified object name (the fully qualified name minus some constant package prefix) of the underlying message base class.
     * 
     * @return the partially qualified object name as a not-null Java String.
     */
    public String get$DataPQON();
    /** Gets the class of the underlying message base class for the data object.
     * 
     * @return the class type of the underlying message base class, never null.
     */
    public Class<? extends D> get$DataClass();
    
    /** Gets a new message object initialized with the Entity classes data.
     *  
     * @throws EnumException, but for future extensions, the more generic ApplicationException is declared.
     * @return a new object of the base class initialized with the data of this instance.
     */
    public D get$Data() throws ApplicationException;
    public void set$Data(D _d);
    
}
