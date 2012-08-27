package de.jpaw.bonaparte.core;

public class ExternalizableConstants {
	protected static boolean nestedObjectsInternally = true; // false = run through serializable, true = as on ASCII format
	protected static final long powersOfTen[] = {
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
	protected static final byte ESCAPE_CHAR = '\005';               // ctrl-E
	protected static final byte NULL_FIELD = '\016';                // ctrl-N

	// variable length integers

	// numeric tokens:21 tokens in sequential order
	protected static final byte NUMERIC_MIN      = (byte)0x21;
	
	protected static final byte INT_ONEBYTE      = (byte)0x21;
	protected static final byte INT_TWOBYTES     = (byte)0x22;
	protected static final byte INT_FOURBYTES    = (byte)0x23;
	protected static final byte INT_EIGHTBYTES   = (byte)0x24;
	
	protected static final byte FRAC_SCALE_0     = (byte)'a';
	protected static final byte FRAC_SCALE_18    = (byte)('a'+18);
	
	// immediate single-byte values: 12 consecutive values -1 .. 10
	protected static final byte INT_MINUS_ONE = '/';                // 0x2F
	protected static final byte INT_ZERO      = '0';                // 0x30
	protected static final byte INT_ONE       = '1';                //
	protected static final byte INT_TWO       = '2';                //
	protected static final byte INT_THREE     = '3';                //
	protected static final byte INT_FOUR      = '4';                //
	protected static final byte INT_FIVE      = '5';                //
	protected static final byte INT_SIX       = '6';                //
	protected static final byte INT_SEVEN     = '7';                //
	protected static final byte INT_EIGHT     = '8';                //
	protected static final byte INT_NINE      = '9';                // 0x39
	protected static final byte INT_TEN       = ':';                // 0x3a
	protected static final byte NUMERIC_MAX   = '@';                // 0x40 is 16

	protected static final byte BINARY_FLOAT  = 'F';
	protected static final byte BINARY_DOUBLE = 'D';

	// non-numeric
	protected static final byte TEXT          = 'T';
	protected static final byte BINARY        = 'B';

}
