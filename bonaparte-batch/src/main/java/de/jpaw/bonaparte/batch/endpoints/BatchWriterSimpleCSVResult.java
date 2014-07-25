package de.jpaw.bonaparte.batch.endpoints;

import de.jpaw.bonaparte.batch.BatchWriter;
import de.jpaw.bonaparte.batch.BatchWriterTextFileAbstract;

public class BatchWriterSimpleCSVResult extends BatchWriterTextFileAbstract implements BatchWriter<Boolean> {

    private String getResult(Boolean data) {
        if (data != null && data.booleanValue())
            return "OK";
        else
            return "ERROR";
    }
    
    @Override
    public void storeResult(int no, Boolean response) throws Exception {
        super.write(no + "," + getResult(response) + "\n");
    }
}
