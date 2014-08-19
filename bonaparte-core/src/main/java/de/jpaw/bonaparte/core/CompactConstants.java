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
 * The CompactConstants class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Defines some constants which are used for the binary / compact data parsers and composers.
 *          A lot of values are represented by a single byte.
 *          
 *          If the first byte is 00-9f or c0-cf, then the whole value is a single byte.
 *          
 *          00 - 1f     integral numbers 0 - 31 (boolean false or true are represented as 0 and 1) 
 *          20 - 7f     1 character Strings or chars (ASCII)  [only space, +, -, ., ,, 0-9, A-Z, a-z ]
 *          
 *          8x  integers 32..47
 *          9x  integers 48..63
 *          
 *          a0          null (any data type)
 *          a1 - ac     -1 to -12
 *          ad          object end
 *          ae          subobject end
 *          af          empty (zero character String, empty Map, Set, List, array with 0 elements)
 *          
 *          bx    ASCII string, 1..16 characters length
 *          cx  positive 2 byte integer:    x(nn) 0..4095
 *          
 *          dx   reserved for floating point formats:
 *          d0   short float (IEEE 754, 16 bit): next 2 bytes define the value
 *          d1   float (IEEE 754, 32 bit): next 4 bytes define the value
 *          d2   double (IEEE 754, 64 bit): next 8 bytes define the value
 *          d3   long double (IEEE 754, 128 bit): next 16 bytes define the value
 *          d4   extended double (non-IEEE, 80 bit), reserved for non-Java languages, 10 bytes follow
 *          d5   compressed element. Next is format, then uncompressed length, then compressed length, then bytes. The element is not null. The contained element can be a string, byte [] or object.
 *          d6   char (2 bytes follow)
 *          d7   UUID (next 8 bytes define the value)
 *          
 *          d8   date (day): 4 bytes following:  YYYY,MM,DD (YYYY in ex (nn) format, see below)
 *          d9   time (int seconds of day)
 *          da   time with millisecond precision (int millis of day)
 *          db   datetime (day + time as seconds of day) (1 + 4 + x byte)
 *          dc   datetime with millis (day + time in millis of day) (1 + 4 + x byte for sensible years)
 *          
 *          dd object backreference (next is positive int says how much back)
 *          de  identifiable object: next is factoryId, then objectId (suggestion: keep object IDs <= 4095) 
 *          df  object: next is String (PQON), then revision. If the string is sent as nullstring, then it is the base object as defined by the ObjectReference
 *          
 *          e0  big integer, next is length in bytes, then mantissa, in 2s complement, with MSB first
 *          e1  long String ASCII (next is length, then bytes)
 *          e2..e8  integer (short, int, long) with 2..8 bytes, next is mantissa, in 2's complement  (5,6,7 currently unused)
 *          
 *          f0  long fractional, next is scale, then big integer of mantissa
 *          f1..f9  fractional, with 1..9 decimal places, next is big integer of mantissa
 *          
 *          // next 3 are optional and will be deleted once all is tested
 *          fa  map begin (next is number of entries)
 *          fb  end of collection
 *          fc  array begin (next is number of entries)
 *          
 *          fd  any length String UTF-16 (next is length (in characters!), then bytes)
 *          
 *          fe   Binary (next: length, then bytes)
 *          ff   any length String UTF-8 (next: length in bytes, then chars, in modified UTF-8)
 *      
 *          TODO: String should actually distinguish by the number of bytes / char and use a different encoding then, to approximate
 *          the number of characters needed. US-only strings should be encoded as single byte strings, if we find at least one
 *          3-byte sequence, let's use UTF-16, otherwise we use UTF8 (slow).
 *          This will slow down some international strings, but be faster and more compact for English, Chinese, and Japanese texts.
 *           
 *          enum: no special token. stored as integer (ordinal) or string
 *          
 *    Some values can be represented by multiple formats (for example numbers 0..63: for example 17 is
 *    0x11
 *    or 0xa0 0x11
 *    or 0xe0 0x01 0x11
 *    
 *    The implementation makes no guarantees that always the short possible form is used. Year numbers for example
 *    are likely to be written always as 2 bytes, because the likelyhood of year numbers in the range 00..63 is very low.
 */

public abstract class CompactConstants extends Settings {
    protected static final String EMPTY_STRING = new String("");

    protected static final int PARENT_SEPARATOR = 0xae;
    protected static final int OBJECT_BEGIN_ID = 0xde;
    protected static final int OBJECT_BEGIN_PQON = 0xdf;
    protected static final int OBJECT_TERMINATOR = 0xad;
    protected static final int OBJECT_AGAIN = 0xdd;
    
    protected static final int NULL_FIELD = 0xa0;
    protected static final int EMPTY_FIELD = 0xaf;
    protected static final int MAP_BEGIN = 0xfa;
    protected static final int COLLECTIONS_TERMINATOR = 0xfb;  // array / set / list / map terminator
    protected static final int ARRAY_BEGIN = 0xfc;

    protected static final int INT_2BYTE = 0xe2;
    protected static final int INT_3BYTE = 0xe3;
    protected static final int INT_4BYTE = 0xe4;
    protected static final int INT_8BYTE = 0xe8;
    protected static final int UNICODE_CHAR = 0xd6;
    protected static final int SHORT_ASCII_STRING = 0xb0;  // 16 consequtive
    protected static final int COMPACT_FLOAT = 0xd1;
    protected static final int COMPACT_DOUBLE = 0xd2;
    protected static final int COMPACT_UUID = 0xd7;
    protected static final int COMPACT_BIGDECIMAL = 0xf0;
    protected static final int COMPACT_BIGINTEGER = 0xe0;
    protected static final int COMPACT_BINARY = 0xfe;

    protected static final int COMPACT_DATE = 0xd8;
    protected static final int COMPACT_TIME = 0xd9;
    protected static final int COMPACT_TIME_MILLIS = 0xda;
    protected static final int COMPACT_DATETIME = 0xdb;
    protected static final int COMPACT_DATETIME_MILLIS = 0xdc;
    
    protected static final int ASCII_STRING = 0xe1;
    protected static final int UTF16_STRING = 0xfd;
    protected static final int UTF8_STRING = 0xff;
    
    protected static final String CHARSET_ASCII = "ISO-8859-1"; // US-ASCII
    protected static final String CHARSET_UTF8 = "UTF-8";
    protected static final String CHARSET_UTF16 = "UTF-16BE";

    protected static final int COMPRESSED = 0xd5;
    protected static final int COMPRESSED_LZ4 = 0;  // first type

}
