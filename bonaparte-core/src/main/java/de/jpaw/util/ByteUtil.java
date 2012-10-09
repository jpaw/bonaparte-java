package de.jpaw.util;

public class ByteUtil {
	public static byte [] deepCopy(byte [] org) {
		if (org == null)
			return null;
		byte [] result = new byte [org.length];
		System.arraycopy(org, 0, result, 0, org.length);
		return result;
	}
}
