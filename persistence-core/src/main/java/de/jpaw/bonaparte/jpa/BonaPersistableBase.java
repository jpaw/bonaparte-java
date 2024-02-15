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

/**
 * Defines the core methods a JPA entity class implements.
 * The classes and their implementations are usually created by the bonaparte add-on DSL BDDL.
 *
 * @author Michael Bischoff
 *
 **/

public interface BonaPersistableBase {
    /** Gets some optional RTTI (runtime type information). If no rtti has been supplied, the rtti of a parent class is returned.
     *
     * @return some numeric value defined in the DSL.
     */
    public int ret$rtti();

    /** Method that allows generic proxy resolution by returning {@code this}. */
    public BonaPersistableBase ret$Self();

    /** method to activate or deactivate a row */
    public void put$Active(boolean _a);
    /** method to query activeness */
    public boolean ret$Active();

    /** method to set an integer version */
    public void put$IntVersion(int _v);
    /** method to query current version.
     * @returns -1 if no version column of type int or Integer in this entity */
    public int ret$IntVersion();


    /** Merges the contents of one entity instance into the current one.
     * Performs a shallow copy.
     */
    public BonaPersistableBase mergeFrom(BonaPersistableBase source);
}
