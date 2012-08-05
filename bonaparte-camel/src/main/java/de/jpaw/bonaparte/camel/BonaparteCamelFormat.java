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
package de.jpaw.bonaparte.camel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.BonaPortable;

/**
 * The BonaparteCamelFormat class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the Apache Camel DataFormat interface using the StringBuilder bonaparte implementation.
 *          Work in progress - needs major rework to provide better separation of outer transmission layer.
 */

public final class BonaparteCamelFormat implements DataFormat {
	private static final Logger logger = LoggerFactory.getLogger(BonaparteCamelFormat.class);
	// configuration
	// private boolean multiRecord = false;   // do a full transmission? Expects an array of objects then!  NO, autodetect! 
	private boolean writeCRs = false;
	private boolean writeEncoding = false;
	// the character set used for backend communication: UTF-8 or ISO-8859-something or windows-125x
    private Charset useCharset = Charset.forName("UTF-8"); // Charset.defaultCharset(); or "windows-1252"
    private int initialBufferSize = 65500;  // start big to avoid frequent reallocation 
	
	
	private void writeOptions(OutputStream stream, MessageComposer w) throws IOException {
		String encoding = "\030E€\031"; 
		if (writeEncoding)
			stream.write(encoding.getBytes(w.getCharset()));
	}

	public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
		StringBuilder work;  // TODO: allocate during new()? Keeps it persistent, but may occupy too much space. Multithreading?
		
		work = new StringBuilder(initialBufferSize);

		MessageComposer w = new StringBuilderComposer(work);
		if (java.util.List.class.isInstance(graph)) {
			//w.startTransmission();
			stream.write('T'-'@'); // transmission begin
			stream.write('N'-'@'); // Null version
			writeOptions(stream, w);
			// TODO: w.writeOptions
			for (Object o : (java.util.List)graph) {
				w.reset();
				w.writeRecord((BonaPortable) o);
				stream.write(work.toString().getBytes(useCharset));
			}
			//w.terminateTransmission();
			stream.write('U'-'@'); // transmission end
			stream.write('Z'-'@'); // transmission end
		} else {
			// assume single record
			writeOptions(stream, w);
			w.reset();
			w.writeRecord((BonaPortable) graph);
			stream.write(work.toString().getBytes(useCharset));
		}
	}

	public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
		// get the bytes, convert to String, parse
		// TODO: avoid byte buffer breaks within UTF-8-sequence!
		boolean isMultiRecord = false;
		byte [] byteBuffer = new byte[initialBufferSize];
		int numbytes = stream.read(byteBuffer, 0, initialBufferSize);
		logger.debug("read {} bytes from the input stream", numbytes);
		if (numbytes == initialBufferSize)
			throw new Exception("multi-reads for big messages not yet supported");
		if (byteBuffer[0] == '\024')   // multi record (transmission)
			isMultiRecord = true;
		
		StringBuilder work = new StringBuilder(new String (byteBuffer, useCharset)); 
		MessageParser w = new StringBuilderParser(work, 0, -1);
		List<BonaPortable> resultSet = w.readTransmission();
		if (isMultiRecord)
			return resultSet;		// which may be empty
		else if (resultSet.size() == 0)
			return null;
		else {
			if (resultSet.size() > 1)
				throw new Exception("more than 1 record without a transmission header!");
			return resultSet.get(0);
		}
	}

	
	
	public boolean isWriteCRs() {
		return writeCRs;
	}

	public void setWriteCRs(boolean writeCRs) {
		this.writeCRs = writeCRs;
	}

	public boolean isWriteEncoding() {
		return writeEncoding;
	}

	public void setWriteEncoding(boolean writeEncoding) {
		this.writeEncoding = writeEncoding;
	}

	public Charset getUseCharset() {
		return useCharset;
	}

	public void setUseCharset(Charset useCharset) {
		this.useCharset = useCharset;
	}

	public int getInitialBufferSize() {
		return initialBufferSize;
	}

	public void setInitialBufferSize(int initialBufferSize) {
		this.initialBufferSize = initialBufferSize;
	}

}
