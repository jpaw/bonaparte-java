package de.jpaw.util;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ByteUtil {
//    private static final Logger LOG = LoggerFactory.getLogger(ByteUtil.class);

    public static byte [] deepCopy(byte [] org) {
		if (org == null)
			return null;
		byte [] result = new byte [org.length];
		System.arraycopy(org, 0, result, 0, org.length);
		return result;
	}
	
	private static String readable(byte[] a, int offset, StringBuilder w, int max) {
	    w.setLength(0);
	    int l = max < 8 ? max : 8;
		for (int i = 0; i < l; ++i) {
			char c = (char) (a[offset + i]);
			w.append(CharTestsASCII.isAsciiPrintable(c) ? c : '.');
		}
		if (max > 8) {
		    l = max < 16 ? max : 16;
		    w.append(' ');
		    for (int i = 8; i < l; ++i) {
		        char c = (char) (a[offset + i]);
		        w.append(CharTestsASCII.isAsciiPrintable(c) ? c : '.');
		    }
		}
		return w.toString();
	}

	private static String hexOrBlank(byte[] a, int i) {
	    if (i >= a.length)
	        return "   ";
	    return String.format("%02x ", a[i]);
	}
	
    /**
     * <code>dump()</code> dumps the contents of a byte array in a readable 2-column format (hex as well as masked ASCII)
     * to the logger at <code>trace</code> logging level.
     * @param a          the byte array to dump
     * @param maxlength  the maximum number of bytes to output to the logger (to avoid megabytes of data)
     */
	public static String dump(byte[] a, int maxlength) {
        StringBuilder w = new StringBuilder(20);
		StringBuilder buff = new StringBuilder(a.length * 5 + 80);
		// output only multiples of 16...
		int i;
		for (i = 0; i < (a.length & ~0x0f); i += 16) {
			if (maxlength > 0 && i > maxlength) {
				buff.append("...");
				return buff.toString();
			}
			buff.append(String.format("%04x: %02x %02x %02x %02x %02x %02x %02x %02x  %02x %02x %02x %02x %02x %02x %02x %02x  %s\n",
							i, a[i + 0], a[i + 1], a[i + 2], a[i + 3],
							a[i + 4], a[i + 5], a[i + 6], a[i + 7], a[i + 8],
							a[i + 9], a[i + 10], a[i + 11], a[i + 12],
							a[i + 13], a[i + 14], a[i + 15], readable(a, i, w, 16)));
		}
		// now possibly one last partial line...
		if (i < a.length) {
            buff.append(String.format("%04x: %s%s%s%s%s%s%s%s %s%s%s%s%s%s%s%s %s\n",
                    i,
                    hexOrBlank(a, i + 0),
                    hexOrBlank(a, i + 1),
                    hexOrBlank(a, i + 2),
                    hexOrBlank(a, i + 3),
                    hexOrBlank(a, i + 4),
                    hexOrBlank(a, i + 5),
                    hexOrBlank(a, i + 6),
                    hexOrBlank(a, i + 7),
                    hexOrBlank(a, i + 8),
                    hexOrBlank(a, i + 9),
                    hexOrBlank(a, i +10),
                    hexOrBlank(a, i +11),
                    hexOrBlank(a, i +12),
                    hexOrBlank(a, i +13),
                    hexOrBlank(a, i +14),
                    hexOrBlank(a, i +15),
                    readable(a, i, w, a.length - i)));
		}
		return buff.toString();
	}

}
