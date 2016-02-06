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

public interface ByteArrayConstants {
//    public static final String MIME_TYPE = MimeTypes.MIME_TYPE_BONAPARTE;
    public static final String EMPTY_STRING = new String("");
    // constants
    public static final byte PLUS_SIGN = '+';
    public static final byte MINUS_SIGN = '-';
    public static final byte DECIMAL_POINT = '.';

    public static final byte FIELD_TERMINATOR = '\006';          // ctrl-F
    public static final byte TRANSMISSION_TERMINATOR = '\025';   // ctrl-U
    public static final byte TRANSMISSION_TERMINATOR2 = '\032';  // ctrl-Z
    public static final byte ARRAY_TERMINATOR = '\001';          // ctrl-A
    public static final byte ARRAY_BEGIN = '\002';               // ctrl-B
    public static final byte TRANSMISSION_BEGIN = '\024';        // ctrl-T
    public static final byte RECORD_BEGIN = '\022';              // ctrl-R
    public static final byte RECORD_OPT_TERMINATOR = '\015';     // ctrl-M
    public static final byte RECORD_TERMINATOR = '\012';         // ctrl-J
    public static final byte PARENT_SEPARATOR = '\020';          // ctrl-P
    public static final byte OBJECT_BEGIN = '\023';              // ctrl-S
    public static final byte OBJECT_TERMINATOR = '\017';         // ctrl-O
    public static final byte OBJECT_AGAIN = '\007';              // ctrl-G
    public static final byte ESCAPE_CHAR = '\005';               // ctrl-E
    public static final byte NULL_FIELD = '\016';                // ctrl-N
    public static final byte MAP_BEGIN = '\036';                 //

    public static final byte BOM1 = (byte)0xef;                 // optional first byte of UTF-8 encoded byte order mark
    public static final byte BOM2 = (byte)0xbb;                 //
    public static final byte BOM3 = (byte)0xbf;                 //
}
