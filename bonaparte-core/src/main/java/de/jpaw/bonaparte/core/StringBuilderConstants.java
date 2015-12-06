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

public interface StringBuilderConstants {
    public static final String MIME_TYPE = MimeTypes.MIME_TYPE_BONAPARTE;
    public static final String EMPTY_STRING = new String("");

    public static final char FIELD_TERMINATOR = '\006'; // ctrl-F
    public static final char TRANSMISSION_TERMINATOR = '\025'; // ctrl-U
    public static final char TRANSMISSION_TERMINATOR2 = '\032'; // ctrl-Z
    public static final char ARRAY_TERMINATOR = '\001'; // ctrl-A
    public static final char ARRAY_BEGIN = '\002'; // ctrl-B
    public static final char TRANSMISSION_BEGIN = '\024'; // ctrl-T
    public static final char RECORD_BEGIN = '\022'; // ctrl-R
    public static final char RECORD_OPT_TERMINATOR = '\015'; // ctrl-M
    public static final char RECORD_TERMINATOR = '\012'; // ctrl-J
    public static final char PARENT_SEPARATOR = '\020'; // ctrl-P
    public static final char OBJECT_BEGIN = '\023'; // ctrl-S
    public static final char OBJECT_TERMINATOR = '\017';         // ctrl-O
    public static final char OBJECT_AGAIN = '\007';              // ctrl-G
    public static final char ESCAPE_CHAR = '\005'; // ctrl-E
    public static final char NULL_FIELD = '\016'; // ctrl-N
    public static final char MAP_BEGIN = '\036';                 //

    public static final char BOM = 0xfeff;                 // possibly encountered at the beginning of a record
}
