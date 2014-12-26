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

import com.google.common.collect.ImmutableMap;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;

/** 
 * Defines the static methods of a BonaPortable, as well as the factory.
 * The class and its implementation is usually created by the bonaparte DSL.
 * 
 * The generics type parameter defines the enclosing class.
 * As this interface is implemented by an enum and Java does not allow generic enums, this cannot be implemented consequently.
 *  
 * @author Michael Bischoff
 *
 **/
public interface BonaPortableClass<T extends BonaPortable> {
    /** factory method, creates a new instance of the embedding class (no args constructor).
     * 
     */
    T newInstance();
    Class<T> getBonaPortableClass();

    int getFactoryId();
    int getId();
    int getRtti();
    String getPqon();
    boolean isFreezable();
    boolean isImmutable();
    String getBundle();
    String getRevision();
    long getSerial();
    ClassDefinition getMetaData();
    BonaPortableClass<? extends BonaPortable> getParent();
    BonaPortableClass<? extends BonaPortable> getReturns();
    BonaPortableClass<? extends BonaPortable> getPrimaryKey();
    
    /** Gets the map of current properties of this class. All properties are defines by the DSL, the returned map will be immutable.
     * 
     * @return the current map of properties, which is never null, but may be empty.
     */
    ImmutableMap<String,String> getPropertyMap();
    
    /** Retrieves a single property from the current map.
     * 
     * @param property the key of the property.
     * @return the property for the given parameter, or null if it does not exist.
     */ 
    String getClassProperty(String property);
    
    String getFieldProperty(String fieldname, String propertyname);
    boolean hasClassProperty(String property);
    boolean hasFieldProperty(String fieldname, String propertyname);
}
