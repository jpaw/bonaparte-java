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

package de.jpaw.bonaparte.coretests.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;

/**
 * The SimpleTestRunner class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          This class performs some simple serialization and subsequent deserialization.
 *          It compares the results produced by different serializers.
 *          It also compares the deserialized object with the original object.
 *          Caveat is that the compare function must be verified separately, because a mismatch
 *          could be a problem in the compare function as well. Moreover, success could also
 *          mistakenly reported for incorrect results, if the compare functions returns true
 *          too often.
 */

public class SimpleTestRunner {
	static private final Charset defaultCharset = Charset.forName("UTF-8");			 // always use UTF-8 unless explicitly requested differently
	
	static private void dumpToFile(String filename, byte [] data) throws Exception {
		OutputStream stream = new FileOutputStream(filename);
		stream.write(data);
		stream.close();
	}

	static public void run(BonaPortable src, boolean doDumpToFile) throws Exception {
		System.out.println("Test " + src.get$PQON() + " starting:");
		System.out.println("composer StringBuilder");
		StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder());
		sbc.reset();
		sbc.writeRecord(src);
		byte [] sbcResult = sbc.getBytes();
		
		System.out.println("composer ByteArray");
		MessageComposer bac = new ByteArrayComposer();
		bac.reset();
		bac.writeRecord(src);
		byte [] bacResult = bac.getBytes();
		
		System.out.println("Length with SBC is " + sbcResult.length + ", length with BAC is " + bacResult.length);
		if (doDumpToFile) {
			dumpToFile("/tmp/" + src.get$PQON() + "-dump-sbc.bin", sbcResult);
			dumpToFile("/tmp/" + src.get$PQON() + "-dump-bac.bin", bacResult);
		}
		
		assert sbcResult.length == bacResult.length : "produced byte data should have the same length";
		assert Arrays.equals(sbcResult, bacResult) : "produced byte data should be identical";
		
		// deserialize again
		System.out.println("parser StringBuilder");
		StringBuilder work = new StringBuilder(new String (bacResult, defaultCharset)); 
		MessageParser w1 = new StringBuilderParser(work, 0, -1);
		BonaPortable dst1 = w1.readRecord();
		assert dst1.getClass() == src.getClass() : "returned obj is of wrong type (StringBuilderParser)"; // assuming we have one class loader only
		assert src.hasSameContentsAs(dst1) : "returned obj is not equal to original one (StringBuilderParser)";
		// the inherited equals() normally does not return true
		//assert src.equals(dst1) : "returned obj is not equal to original one (StringBuilderParser) (with equals())";
		
		// alternate deserializer
		System.out.println("parser ByteArray");
		MessageParser w2 = new ByteArrayParser(sbcResult, 0, -1);
		BonaPortable dst2 = w2.readRecord();
		assert dst2.getClass() == src.getClass() : "returned obj is of wrong type (ByteArrayParser)"; // assuming we have one class loader only
		assert src.hasSameContentsAs(dst2) : "returned obj is not equal to original one (ByteArrayParser)";
		// the inherited equals() normally does not return true
		//assert src.equals(dst2) : "returned obj is not equal to original one (ByteArrayParser) (with equals())";
	}
}
