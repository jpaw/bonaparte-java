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

import java.io.Serializable;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.util.ApplicationException;

/** 
 * Defines the methods a JPA entity class implements.
 * The classes and their implementations are usually created by the bonaparte add-on DSL BDDL.
 * This is an interface using generics for KEY, DATA and TRACKING types. If there is no tracking, Object is used for that.
 *  
 * @author Michael Bischoff
 *
 **/
public interface BonaPersistable<K extends Serializable, D extends BonaPortable, T extends BonaPortable> {
    /** Gets some optional RTTI (runtime type information). If no rtti has been supplied, the rtti of a parent class is returned.
     * 
     * @return some numeric value defined in the DSL.
     */
	public int get$rtti();
    /** Gets the class of the underlying key class for the data object.
     * 
     * @return the class type of the underlying key class, never null.
     */
    public Class<K> get$KeyClass();
    
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
    
    /** Gets the partially qualified object name (the fully qualified name minus some constant package prefix) of the underlying message base class.
     * 
     * @return the partially qualified object name as a not-null Java String.
     */
    public String get$TrackingPQON();
    /** Gets the class of the underlying message base class for the data object.
     * 
     * @return the class type of the underlying message base class, never null.
     */
    public Class<T> get$TrackingClass();
    
    /** Gets a new message object initialized with the Entity classes data.
     *  
     * @throws EnumException (if key contains an enum), but for future extensions, the more generic ApplicationException is declared.
     * @return a new object of the base class initialized with the data of this instance.
     */
    public K get$Key() throws ApplicationException;
    public void set$Key(K _d);
    
    /** Gets a new message object initialized with the Entity classes data.
     *  
     * @throws EnumException, but for future extensions, the more generic ApplicationException is declared.
     * @return a new object of the base class initialized with the data of this instance.
     */
    public D get$Data() throws ApplicationException;
    public void set$Data(D _d);
    
    /** Gets the tracking columns (if any) of the data of this instance.
     *  
     * @return a new object of the base class initialized with the tracking data of this instance, or null if no tracking data exists.
     */
    public T get$Tracking() throws ApplicationException;
    public void set$Tracking(T _d);
    
    /** method to activate or deactivate a row */ 
    public void set$Active(boolean _a);
    /** method to query activeness */ 
    public boolean get$Active();
    
    /** method to set an integer version */ 
    public void set$IntVersion(int _v);
    /** method to query current version.
     * @returns -1 if no version column of type int or Integer in this entity */ 
    public int get$IntVersion();
}
