package de.jpaw.bonaparte.batch.tests;

import de.jpaw.bonaparte.batch.BatchMain;
import de.jpaw.bonaparte.batch.endpoints.BatchProcessorFactoryIdentity;
import de.jpaw.bonaparte.batch.endpoints.BatchReaderTextFile;
import de.jpaw.bonaparte.batch.endpoints.BatchWriterTextFile;

public class BatchTextFileCopyST {
    public static void main(String [] args) throws Exception {
        new BatchMain<String,String>().runST(args,
                new BatchReaderTextFile(),
                new BatchWriterTextFile(),
                new BatchProcessorFactoryIdentity<String>()
                );
    }
}
