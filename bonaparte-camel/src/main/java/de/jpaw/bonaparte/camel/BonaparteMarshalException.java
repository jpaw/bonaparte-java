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
package de.jpaw.bonaparte.camel;

/**
 * The BonaparteMarshalException class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Work in progress - needs major rework / rewrite from scratch
 */

public class BonaparteMarshalException extends Exception {
    private static final long serialVersionUID = 6578705245543364726L;
    private String message;
    
    static public final String OBJECT_NOT_OF_TYPE_BONAPARTE             = "Cannot marshal a generic object into BONAPARTE format";
    static public final String OBJECT_NOT_OF_TYPE_LIST                  = "Want a list";
    
    public BonaparteMarshalException(String message) {
        this.message        = message;
    }
    
    public String toString() {
        return "Bonaparte exception: " + message;
    }
}