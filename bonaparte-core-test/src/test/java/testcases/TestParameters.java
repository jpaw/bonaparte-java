package testcases;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.coretests.initializers.FillParameterTests;
import de.jpaw.bonaparte.pojos.tests1.Parameters;

public class TestParameters {
	static private final Charset defaultCharset = Charset.forName("UTF-8");			 // always use UTF-8 unless explicitly requested differently
	
	@Test
	public void testParameters1() throws Exception {
		Parameters src = FillParameterTests.test1();
		src.setTestNoTruncate("harmless");
		
		StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder());
		sbc.reset();
		sbc.writeRecord(src);
		byte [] sbcResult = sbc.getBytes();
		
		MessageComposer bac = new ByteArrayComposer();
		bac.reset();
		bac.writeRecord(src);
		byte [] bacResult = bac.getBytes();

		System.out.println("Length with SBC is " + sbcResult.length + ", length with BAC is " + bacResult.length);
		assert sbcResult.length == bacResult.length : "produced byte data should have the same length";
		assert Arrays.equals(sbcResult, bacResult) : "produced byte data should be identical";

		// deserialize again
		System.out.println("parser StringBuilder");
		StringBuilder work = new StringBuilder(new String (bacResult, defaultCharset)); 
		MessageParser w1 = new StringBuilderParser(work, 0, -1);
		BonaPortable dst1 = w1.readRecord();
		assert dst1.getClass() == src.getClass() : "returned obj is of wrong type (StringBuilderParser)"; // assuming we have one class loader only
		Parameters dst1p = (Parameters)dst1;
		
		// alternate deserializer
		System.out.println("parser ByteArray");
		MessageParser w2 = new ByteArrayParser(sbcResult, 0, -1);
		BonaPortable dst2 = w2.readRecord();
		assert dst2.getClass() == src.getClass() : "returned obj is of wrong type (ByteArrayParser)"; // assuming we have one class loader only
		assert dst1.hasSameContentsAs(dst2) : "returned obj is not equal to original one (ByteArrayParser)";
		Parameters dst2p = (Parameters)dst2;
		
		// extra tests
		assert src.getTestNoTrim().equals(dst1p.getTestNoTrim());
		assert !src.getTestTrim().equals(dst1p.getTestTrim());
		assert dst1p.getTestTrim().equals("no trim");
		assert dst1p.getTestTruncate().equals("I am a string which ");
	}
	
	@Test
	public void testParametersTruncate() throws Exception {
		Parameters src = FillParameterTests.test1();
		
		StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder());
		sbc.reset();
		sbc.writeRecord(src);
		byte [] sbcResult = sbc.getBytes();
		
		MessageComposer bac = new ByteArrayComposer();
		bac.reset();
		bac.writeRecord(src);
		byte [] bacResult = bac.getBytes();

		System.out.println("Length with SBC is " + sbcResult.length + ", length with BAC is " + bacResult.length);
		assert sbcResult.length == bacResult.length : "produced byte data should have the same length";
		assert Arrays.equals(sbcResult, bacResult) : "produced byte data should be identical";

		// deserialize again
		System.out.println("parser StringBuilder");
		StringBuilder work = new StringBuilder(new String (bacResult, defaultCharset)); 
		MessageParser w1 = new StringBuilderParser(work, 0, -1);
		try {
			BonaPortable dst1 = w1.readRecord();
			assert false : "Should have thrown exception due to field too long";
		} catch (MessageParserException e) {
			assert(e.getErrorCode() == MessageParserException.STRING_TOO_LONG);
			System.out.println("got the expected error");
		}
		
		// alternate deserializer
		System.out.println("parser ByteArray");
		MessageParser w2 = new ByteArrayParser(sbcResult, 0, -1);
		try {
			BonaPortable dst2 = w2.readRecord();
			assert false : "Should have thrown exception due to field too long";
		} catch (MessageParserException e) {
			assert(e.getErrorCode() == MessageParserException.STRING_TOO_LONG);
			System.out.println("got the expected error");
		}
	}
}
