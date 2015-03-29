package de.jpaw.bonaparte.core;

public interface ExternalizableConstants {
    public static boolean nestedObjectsInternally = true; // false = run through serializable, true = as on ASCII format
    public static final long powersOfTen[] = {
        1L,
        10L,
        100L,
        1000L,
        10000L,
        100000L,
        1000000L,
        10000000L,
        100000000L,
        1000000000L,
        10000000000L,
        100000000000L,
        1000000000000L,
        10000000000000L,
        100000000000000L,
        1000000000000000L,
        10000000000000000L,
        100000000000000000L,
        1000000000000000000L
    };

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
    public static final byte ESCAPE_CHAR = '\005';               // ctrl-E
    public static final byte NULL_FIELD = '\016';                // ctrl-N
    public static final byte MAP_BEGIN = '\036';                 //

    // variable length integers

    // numeric tokens:21 tokens in sequential order
    public static final byte NUMERIC_MIN      = (byte)0x21;

    public static final byte INT_ONEBYTE      = (byte)0x21;
    public static final byte INT_TWOBYTES     = (byte)0x22;
    public static final byte INT_FOURBYTES    = (byte)0x23;
    public static final byte INT_EIGHTBYTES   = (byte)0x24;

    public static final byte FRAC_SCALE_0     = (byte)'a';
    public static final byte FRAC_SCALE_18    = (byte)('a'+18);

    // immediate single-byte values: 12 consecutive values -1 .. 10
    public static final byte INT_MINUS_ONE = '/';                // 0x2F
    public static final byte INT_ZERO      = '0';                // 0x30
    public static final byte INT_ONE       = '1';                //
    public static final byte INT_TWO       = '2';                //
    public static final byte INT_THREE     = '3';                //
    public static final byte INT_FOUR      = '4';                //
    public static final byte INT_FIVE      = '5';                //
    public static final byte INT_SIX       = '6';                //
    public static final byte INT_SEVEN     = '7';                //
    public static final byte INT_EIGHT     = '8';                //
    public static final byte INT_NINE      = '9';                // 0x39
    public static final byte INT_TEN       = ':';                // 0x3a
    public static final byte NUMERIC_MAX   = '@';                // 0x40 is 16

    public static final byte BINARY_FLOAT  = 'F';
    public static final byte BINARY_DOUBLE = 'D';

    // non-numeric
    public static final byte TEXT          = 'T';
    public static final byte BINARY        = 'B';

}
