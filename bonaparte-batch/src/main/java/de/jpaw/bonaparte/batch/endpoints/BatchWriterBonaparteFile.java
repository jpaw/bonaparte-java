package de.jpaw.bonaparte.batch.endpoints;

import de.jpaw.batch.api.BatchWriter;
import de.jpaw.batch.impl.BatchWriterTextFileAbstract;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.StringBuilderComposer;

public class BatchWriterBonaparteFile extends BatchWriterTextFileAbstract implements BatchWriter<BonaPortable> {
    private StringBuilder buff = new StringBuilder(10000);
    private StringBuilderComposer sbc = new StringBuilderComposer(buff);    // share this across invocations
    
    @Override
    public void accept(int no, BonaPortable response) throws Exception {
        sbc.reset();
        sbc.writeRecord(response);
        write(buff.toString());
        sbc.reset();
    }
}
