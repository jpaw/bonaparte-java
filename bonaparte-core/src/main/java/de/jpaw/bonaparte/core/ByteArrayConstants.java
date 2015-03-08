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
 * The ByteArrayConstants class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Defines some constants which are used by Byte(Array) based parsers and composers.
 */

public abstract class ByteArrayConstants extends Settings {
    protected static final String EMPTY_STRING = new String("");
    // constants
    protected static final byte PLUS_SIGN = '+';
    protected static final byte MINUS_SIGN = '-';
    protected static final byte DECIMAL_POINT = '.';

    protected static final byte FIELD_TERMINATOR = '\006';          // ctrl-F
    protected static final byte TRANSMISSION_TERMINATOR = '\025';   // ctrl-U
    protected static final byte TRANSMISSION_TERMINATOR2 = '\032';  // ctrl-Z
    protected static final byte ARRAY_TERMINATOR = '\001';          // ctrl-A
    protected static final byte ARRAY_BEGIN = '\002';               // ctrl-B
    protected static final byte TRANSMISSION_BEGIN = '\024';        // ctrl-T
    protected static final byte RECORD_BEGIN = '\022';              // ctrl-R
    protected static final byte RECORD_OPT_TERMINATOR = '\015';     // ctrl-M
    protected static final byte RECORD_TERMINATOR = '\012';         // ctrl-J
    protected static final byte PARENT_SEPARATOR = '\020';          // ctrl-P
    protected static final byte OBJECT_BEGIN = '\023';              // ctrl-S
    protected static final byte OBJECT_TERMINATOR = '\017';         // ctrl-O
    protected static final byte OBJECT_AGAIN = '\007';              // ctrl-G
    protected static final byte ESCAPE_CHAR = '\005';               // ctrl-E
    protected static final byte NULL_FIELD = '\016';                // ctrl-N
    protected static final byte MAP_BEGIN = '\036';                 //

    protected static final byte BOM1 = (byte)0xef;                 // optional first byte of UTF-8 encoded byte order mark
    protected static final byte BOM2 = (byte)0xbb;                 //
    protected static final byte BOM3 = (byte)0xbf;                 //
}
