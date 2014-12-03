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

import java.math.BigDecimal;
import java.util.Map;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

/** 
 * Defines the methods any object which should be serialized into the bonaparte format must implement.
 * The class and its implementation is usually created by the bonaparte DSL.
 *  
 * @author Michael Bischoff
 *
 **/
public interface BonaPortable extends BonaCustom {
    
    /** Return the metadata provider. (replacement for all class$xx methods)
     * 
     * @returns
     */
    public BonaPortableClass<? extends BonaPortable> get$BonaPortableClass();
    
    /** Gets some optional RTTI (runtime type information). If no rtti has been supplied, the rtti of a parent class is returned.
     * 
     * @return some numeric value defined in the DSL.
     */
    public int get$rtti();
    
    /** Retrieves a single property from the current map.
     * 
     * @param id the key of the property.
     * @return the property for the given parameter, or null if it does not exist.
     */    
    @Deprecated
    public String get$Property(String id);
    
    /** Gets the map of current properties of this class. All properties are defines by the DSL, the returned map will be immutable.
     * 
     * @return the current map of properties, which is never null, but may be empty.
     */
    @Deprecated
    public Map<String,String> get$PropertyMap();
    
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
    
    
    /** Can be invoked to apply a String converter to all String typed fields in the object, parent objects, and included child objects. */
    public void treeWalkString(DataConverter<String, AlphanumericElementaryDataItem> _cvt, boolean descend);

    /** Can be invoked to apply a BigDecimal converter to all BigDecimal typed fields in the object, parent objects, and included child objects. */
    public void treeWalkBigDecimal(DataConverter<BigDecimal, NumericElementaryDataItem> _cvt, boolean descend);

    /** Can be invoked to apply an Object converter to all fields in the object, and potentially also included child objects. */
    public void treeWalkObject(DataConverter<Object, FieldDefinition> _cvt, boolean descend);

    /** Can be invoked to apply an Object converter to all fields in the object, and potentially also included child objects. */
    public void treeWalkBonaPortable(DataConverter<BonaPortable,ObjectReference> _cvt, boolean descend);
    

    /** A BonaPortable can become immutable, by locking all setters.
     * This mimics some "in place" builder, allows inheritance and avoids the duplicate object definition (by the builder).
     * An object, once frozen, cannot be "unfrozen" (that would defeat the purpose), therefore immutability is granted.
     * If mutable copies are desired, either shallow or deep copies can be created, which are created in unfrozen state.
     * The freeze() method transitions an object into immutable state. The method is not thread-safe, i.e. the freeze should
     * always be performed on a private copy. Once frozen, the object may be shared with other threads. Use get$frozenClone() to
     * get an immutable copy if the previous object was shared with other threads or has other references in this thread.
     */
    void freeze();
    
    /** Returns information, if this object is frozen.
     * If the object is not frozen, it could still have frozen components.
     * If the object is frozen, all components are guaranteed to be frozen as well.
     * 
     * @return true if the object and all its components are frozen, false otherwise.
     * 
     */
    boolean is$Frozen();
    
    /** Obtain a mutable clone of a frozen object. 
     * If deepCopy is set, he object will be recursively mutable, otherwise just the initial layer. */
    BonaPortable get$MutableClone(boolean deepCopy, boolean unfreezeCollections) throws ObjectValidationException;    
    
    /** Obtain an immutable clone of a (possibly) mutable object. */
    BonaPortable get$FrozenClone() throws ObjectValidationException;
}
