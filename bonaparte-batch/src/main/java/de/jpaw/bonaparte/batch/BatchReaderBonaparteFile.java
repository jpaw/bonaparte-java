package de.jpaw.bonaparte.batch;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.StringBuilderParser;

public class BatchReaderBonaparteFile extends BatchReaderTextFileAbstract implements BatchReader<BonaPortable> {
	
	public void produceTo(BatchMainCallback<BonaPortable> whereToPut) throws Exception {
		StringBuilder buffer = new StringBuilder(10000);  // have a buffer which persists a bit longer to avoid GC overhead
		
		// while data is available, insert it into the queue
		String line;
		while ((line = getNext()) != null) {
			// convert the line into a Bonaportable
			buffer.setLength(0);
			buffer.append(line);
            buffer.append("\n");
            StringBuilderParser sbp = new StringBuilderParser(buffer, 0, buffer.length());
            BonaPortable record = sbp.readRecord();			
            whereToPut.scheduleForProcessing(record);
		}
	}
}
