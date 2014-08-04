package de.jpaw.util;

/** Provides min and max. limits for integral numbers, if their number of digits is provided. */
public class IntegralLimits {
    // these should be const as well!
    public static final byte  [] BYTE_MIN_VALUES  = { 0, (byte)-9, (byte)-99, Byte.MIN_VALUE };
    public static final byte  [] BYTE_MAX_VALUES  = { 0, (byte)9, (byte)99, Byte.MAX_VALUE };
    public static final short [] SHORT_MIN_VALUES = { 0, (short)-9, (short)-99, (short)-999, (short)-9999, Short.MIN_VALUE };
    public static final short [] SHORT_MAX_VALUES = { 0, (short)9, (short)99, (short)999, (short)9999, Short.MAX_VALUE };
    public static final int   [] INT_MIN_VALUES   = { 0, -9, -99, -999, -9999, -99999, -999999, -9999999, -99999999, -999999999, Integer.MIN_VALUE };
    public static final int   [] INT_MAX_VALUES   = { 0, 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };
    public static final long  [] LONG_MIN_VALUES  = { 0, -9, -99, -999, -9999, -99999, -999999, -9999999, -99999999, -999999999,
        -9999999999L,
        -99999999999L,
        -999999999999L,
        -9999999999999L,
        -99999999999999L,
        -999999999999999L,
        -9999999999999999L,
        -99999999999999999L,
        -999999999999999999L,
        Long.MIN_VALUE
    };
    public static final long  [] LONG_MAX_VALUES  = { 0, 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999,
        9999999999L,
        99999999999L,
        999999999999L,
        9999999999999L,
        99999999999999L,
        999999999999999L,
        9999999999999999L,
        99999999999999999L,
        999999999999999999L,
        Long.MAX_VALUE
    };
    
    /** scales for implied fractional digits. */
    public static final double  [] IMPLICIT_SCALES  = { 1.0, 1.0e-1, 1.0e-2, 1.0e-3, 1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7, 1.0e-8, 1.0e-9,
        1.0e-10, 1.0e-11, 1.0e-12, 1.0e-13, 1.0e-14, 1.0e-15, 1.0e-16, 1.0e-17, 1.0e-18, 1.0e-19 // last is unused
    };
    public static final double  [] EXPONENTS  = { 1.0, 1.0e+1, 1.0e+2, 1.0e+3, 1.0e+4, 1.0e+5, 1.0e+6, 1.0e+7, 1.0e+8, 1.0e+9,
        1.0e+10, 1.0e+11, 1.0e+12, 1.0e+13, 1.0e+14, 1.0e+15, 1.0e+16, 1.0e+17, 1.0e+18, 1.0e+19 // last is unused
    };
}
