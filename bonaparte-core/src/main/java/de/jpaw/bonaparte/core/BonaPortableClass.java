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
    /** Factory method, creates a new instance of the embedding class (using the no args constructor, i.e. all fields initialized to null). */
    T newInstance();
    
    /** Returns the Java Class<?> of the described BonaPortable. */
    Class<T> getBonaPortableClass();

    /** Returns the ID of the factory for deserialization (Hazelcast alternative to use of the String class name in the serialized form). */
    int getFactoryId();
    
    /** Returns the ID of the class within the factory for deserialization (Hazelcast alternative to use of the String class name in the serialized form). */
    int getId();
    
    /** Returns the run time type information, usually used for some offset for surrogate keys or determining customization. */
    int getRtti();
    
    /** Returns the partially qualified name, a substring of the canonical name, but with a common prefix omitted for brevity of the serialized form, required to identify the class. */ 
    String getPqon();
    
    /** Returns true if the class can be rendered unchangeable. */
    boolean isFreezable();
    
    /** Returns true of the class is immutable by nature. */
    boolean isImmutable();
    
    /** Returns the bundle ID of the package (corresponds to a deployable unit or JAR ID). */ 
    String getBundle();
    
    /** Returns the change revision of this class. */
    String getRevision();
    
    /** returns the otherwise private SerialVersionUid of related class. */
    long getSerial();
    
    /** Returns the BonaPortable presenation of the class's meta data. */
    ClassDefinition getMetaData();
    
    /** Returns null if the class does not inherit any other class, or the BonaPortableClass of the parent. */
    BonaPortableClass<? extends BonaPortable> getParent();
    
    /** Returns BonaPortableClass of the class defined as the related return type.
     *  Transitive, inherited classes will return return their parent's return type, unless they refined it to a subclass of it. */
    BonaPortableClass<? extends BonaPortable> getReturns();
    
    /** Returns BonaPortableClass of the class defined as the related primary key type.
     *  Transitive, inherited classes will return return their parent's key type. Redefinition is not possible. */
    BonaPortableClass<? extends BonaPortable> getPrimaryKey();
    
    /** Gets the map of current properties of this class. All properties are defines by the DSL, the returned map will be immutable.
     * 
     * @return the current map of properties, which is never null, but may be empty.
     */
    ImmutableMap<String,String> getPropertyMap();
    
    /** Retrieves a single property from the current map.
     * Field properties are stored as fieldname "." propertyname.
     * 
     * @param property the key of the property.
     * @return the property for the given parameter, or null if it does not exist. Returns an empty String for properties defined without a value.
     * 
     * @Since 2.3.4
     */ 
    String getProperty(String property);
}
