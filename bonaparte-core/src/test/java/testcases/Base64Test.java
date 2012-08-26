package testcases;

import java.util.Arrays;

import de.jpaw.util.Base64;
import de.jpaw.util.ByteBuilder;

import org.testng.annotations.Test;

public class Base64Test {

	private static void checkSame(byte[] a, byte[] b) throws Exception {
		for (int i = 0; i < a.length; ++i)
			if (a[i] != b[i])
				throw new Exception("Arrays differ at byte pos " + i);
	}
	@Test
	public void testEncodeDecode() throws Exception {
		// test runs with different padding, include the trivial zero length case 
		for (int i = 0; i <= 12; ++i) {
			System.out.println("Testing length " + i);
			byte [] rawdata = new byte [i];
			Arrays.fill(rawdata, (byte)99);
			ByteBuilder target = new ByteBuilder();
			Base64.encodeToByte(target, rawdata, 0, rawdata.length);

			byte [] base64decoded = Base64.decodeFast(target.getBytes());
			assert base64decoded.length == rawdata.length;
			checkSame(base64decoded, rawdata);


			base64decoded = Base64.decode(target.getBytes(), 0, target.length());
			assert base64decoded.length == rawdata.length;
			checkSame(base64decoded, rawdata);

		}

	}

}
