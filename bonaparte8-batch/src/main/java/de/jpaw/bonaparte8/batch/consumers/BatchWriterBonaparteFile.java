package de.jpaw.bonaparte8.batch.consumers;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw8.batch.consumers.impl.BatchWriterTextFileAbstract;

public class BatchWriterBonaparteFile extends BatchWriterTextFileAbstract<BonaPortable> {
    private final StringBuilder buff = new StringBuilder(8000);
    private final StringBuilderComposer sbc = new StringBuilderComposer(buff);    // share this across invocations

    @Override
    public void store(BonaPortable response, int no) {
        sbc.reset();
        sbc.writeRecord(response);
        write(buff.toString());
        sbc.reset();
    }
}
