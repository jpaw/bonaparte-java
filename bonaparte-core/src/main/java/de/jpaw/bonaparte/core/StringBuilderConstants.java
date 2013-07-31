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

/**
 * The StringBuilderConstants class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Defines some constants which are used by both the StringBuilder parser and composer.
 */

public abstract class StringBuilderConstants extends Settings {
    protected static final String EMPTY_STRING = new String("");

    protected static final char FIELD_TERMINATOR = '\006'; // ctrl-F
    protected static final char TRANSMISSION_TERMINATOR = '\025'; // ctrl-U
    protected static final char TRANSMISSION_TERMINATOR2 = '\032'; // ctrl-Z
    protected static final char ARRAY_TERMINATOR = '\001'; // ctrl-A
    protected static final char ARRAY_BEGIN = '\002'; // ctrl-B
    protected static final char TRANSMISSION_BEGIN = '\024'; // ctrl-T
    protected static final char RECORD_BEGIN = '\022'; // ctrl-R
    protected static final char RECORD_OPT_TERMINATOR = '\015'; // ctrl-M
    protected static final char RECORD_TERMINATOR = '\012'; // ctrl-J
    protected static final char PARENT_SEPARATOR = '\020'; // ctrl-P
    protected static final char OBJECT_BEGIN = '\023'; // ctrl-S                NEW
    protected static final char OBJECT_AGAIN = '\007';              // ctrl-G
    protected static final char ESCAPE_CHAR = '\005'; // ctrl-E
    protected static final char NULL_FIELD = '\016'; // ctrl-N                  NEW
    protected static final char MAP_BEGIN = '\036';                 //
}
