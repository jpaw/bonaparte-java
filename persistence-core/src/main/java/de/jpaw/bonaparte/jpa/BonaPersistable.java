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

/**
 * Defines the methods a JPA entity class implements.
 * The classes and their implementations are usually created by the bonaparte add-on DSL BDDL.
 * This is an interface using generics for KEY, DATA and TRACKING types. If there is no tracking, "Object" should be used instead.
 *
 * @author Michael Bischoff
 *
 **/
public interface BonaPersistable<K extends Serializable, D extends BonaPortable, T extends BonaPortable> extends
    BonaPersistableNoData<K, T>, BonaPersistableData<D> {
}
