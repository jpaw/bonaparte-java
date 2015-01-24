package de.jpaw.bonaparte.batch;

import de.jpaw.batch.endpoints.BatchReaderTextFile;
import de.jpaw.batch.endpoints.BatchWriterDevNull;
import de.jpaw.batch.impl.BatchExecutorUnthreaded;
import de.jpaw.bonaparte.pojos.meta.CsvInput;

/** Utility executable which analyzes a pipe delimited file and outputs statistics, with the intention to create a bon file suitable to read it. */ 
public class AnalyzerCSVMain {
    public static String delimiter = "\\|";     // can overwrite this from some unit test...
    
    public static void main(String[] args) throws Exception {
        new BatchExecutorUnthreaded<String, String>().run(args,
                new BatchReaderTextFile(),
                new BatchWriterDevNull<String>(),
                new AnalyzerWorkerFactory(CsvInput.class$MetaData(), delimiter));
    }
}
