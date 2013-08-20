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
import java.util.concurrent.ConcurrentMap;

import de.jpaw.bonaparte.pojos.meta.ParsedFoldingComponent;
import de.jpaw.util.ApplicationException;

/** 
 * Defines the methods any object which should be serialized into the bonaparte format must implement.
 * The class and its implementation is usually created by the bonaparte DSL.
 *  
 * @author Michael Bischoff
 *
 **/
public interface BonaPortable extends Serializable {
    /** Gets some optional RTTI (runtime type information). If no rtti has been supplied, the rtti of a parent class is returned.
     * 
     * @return some numeric value defined in the DSL.
     */
	public int get$rtti();
    /** Gets the partially qualified object name (the fully qualified name minus some constant package prefix).
     * This is a constant string (static final), but defined as a member function in order to be able to declare it in the interface.
     * 
     * @return the partially qualified object name as a not-null Java String.
     */
    public String get$PQON();
    /** Gets the object revision (version number) as defined in the DSL.
     * This is a constant string (static final), but defined as a member function in order to be able to declare it in the interface.
     * The expression <code>getClass().getCanonicalName()</code> consists of some package prefix concatenated with the return value of this function. 
     * @return the revision as Java String, or null if it has not been defined (which usually corresponds to the initial revision of an object).
     */
    public String get$Revision();
    /** Gets the partially qualified object name of this object's parent, or null if the object does not extend another object.
     * This is a constant string (static final), but defined as a member function in order to be able to declare it in the interface.
     * The expression <code>getClass().getCanonicalName()</code> applied to the object's parent consists of some package prefix concatenated with the return value of this function. 
     *  
     * @return the partially qualified object name of the parent class as a Java String, or null if the object has no explicit superclass.
     */
    public String get$Parent();
    /** Gets the bundle information as defined in the DSL, or null. Bundles do not yet have any functional effect, they are reserved to allow the grouping into OSGi bundles in the future. 
     * Therefore, do not yet use this feature.
     * This is a constant string (static final), but defined as a member function in order to be able to declare it in the interface.
     *  
     * @return the bundle as defined in the DSL as a Java String, or null, if no bundle has been defined for objects of this class.
     */
    public String get$Bundle();
    /** Gets the Java serialization serial number. 
     * Shortcut to access the data for an instance of the class.
     *  
     * @return the serial UID, which is a static private class variable.
     */
    public long get$Serial();
    
    /** Retrieves a single property from the current map.
     * 
     * @param id the key of the property.
     * @return the property for the given parameter, or null if it does not exist.
     */    
    public String get$Property(String id);
    
    /** Gets the map of current properties of this class. Normally all properties are defines by the DSL, but it is explicitly allowed 
     * to add or modify properties during runtime (for example loading some from a database setup or properties file). For this reason,
     * the concurrent implementation of the map has been selected.
     * 
     * @return the current map of properties, which is never null, but may be empty.
     */
    public ConcurrentMap<String,String> get$PropertyMap();
    
    /** Gets the defined class of a "return type" if it has been defined for this object or one of its superclasses. Returns null if none has defined a return type. */
    public Class<? extends BonaPortable> get$returns();
    
    /** Gets the defined class of a "pk type" if it has been defined for this object or one of its superclasses. Returns null if none has defined a pk type. */
    public Class<? extends BonaPortable> get$pk();
    
    /** Serializes this object into the format implemented by the MessageComposer parameter. The method will invoke methods of the MessageComposer interface for every member field, and also for some metadata. Class headers itself are assumed to have been serialized before.
     *  Different implementations are provided with the bonaparte library, for ASCII-like formats (bonaparte) or binary formats plugging into the standard Java {@link java.io.Serializable}/{@link java.io.Externalizable} interface.
     *  
     * @param w the implementation of the serializer.
     * @throws E is usually either {@link RuntimeException}, for serializers writing to in-memory buffers, where no checked exceptions are thrown, or {@link java.io.IOException}, for serializers writing to streams. 
     */
    public <E extends Exception> void serializeSub(MessageComposer<E> w) throws E;
    /** Parses data from a stream or in-memory buffer into a preallocated object.
     * The reference to the IO stream or memory sits in the {@link MessageParser} parameter.
     * Parsers for different serialization formats have been implemented, corresponding to the serializer implementations.
     * 
     * @param p the implementation of the message parser. The generic type E is an exception which is thrown in case of I/O errors or parsing problems. Current implementations use either {@link java.io.IOException} as type for E, or {@link MessageParserException}. 
     * @throws E
     */
    public <E extends Exception> void deserialize(MessageParser<E> p) throws E;
    /** An implementation of <code>equals</code>, which receives an object of the BonaPortable type as a parameter.
     *  
     * @param that the object to compare.
     * @return true, if the objects have the same content, false otherwise.
     */
    public boolean hasSameContentsAs(BonaPortable that);
    /** Will provide an explicit implementation of object validation, similar to JSR 303 Bean Validation. This is still work in progress, please use the reflection based validation for now.
     * 
     * @throws ObjectValidationException
     */
    public void validate() throws ObjectValidationException;
    
    /** Provides a shallow copy of the (super)class specified as parameter.
     * 
     * @throws IllegalArgumentException  if no superclass of the requested type exists
     */
    public <T extends BonaPortable> T copyAs(Class<T> desiredSuperType);
    
    
    /** Serializes this object using a mapping. Not all fields are output, and not in the sequence they are declared in the class.
     * In addition, specific indexes can be selected for arrays or Lists.
     *  
     * @param w the implementation of the serializer.
     * @throws E is usually either {@link RuntimeException}, for serializers writing to in-memory buffers, where no checked exceptions are thrown, or {@link java.io.IOException}, for serializers writing to streams. 
     */
    public <E extends Exception> void foldedOutput(MessageComposer<E> w, ParsedFoldingComponent pfc) throws E;
    
    /** Can be invoked to apply a String converter to all String typed fields in the object, parent objects, and included child objects. */
    public void treeWalkString(StringConverter _cvt);

}
