package de.jpaw.bonaparte.batch.endpoints;

import de.jpaw.bonaparte.batch.BatchWriter;
import de.jpaw.bonaparte.batch.BatchWriterTextFileAbstract;

public class BatchWriterTextFile extends BatchWriterTextFileAbstract implements BatchWriter<String> {

	@Override
	public void storeResult(int no, String response) throws Exception {
		super.write(response);
	}
}
