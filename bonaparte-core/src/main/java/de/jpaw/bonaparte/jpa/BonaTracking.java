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
 * Defines the tracking data related methods a JPA entity class implements.
 * The classes and their implementations are usually created by the bonaparte add-on DSL BDDL.
 * This is an interface using generics for the TRACKING type. If there is no tracking, "Object" should be used instead.
 *  
 * @author Michael Bischoff
 *
 **/

public interface BonaTracking <T extends BonaPortable> {
    
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
    
    
    /** Gets the tracking columns (if any) of the data of this instance.
     *  
     * @return a new object of the base class initialized with the tracking data of this instance, or null if no tracking data exists.
     */
    public T get$Tracking() throws ApplicationException;
    public void set$Tracking(T _d);
    
}
