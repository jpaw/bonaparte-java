package de.jpaw.bonaparte.batch.unittests;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.batch.tests.BatchTextFileCopyMT;
import de.jpaw.bonaparte.batch.tests.BatchTextFileCopyST;

public class SingleRequest {
    @Test
    public void testTmpST() throws Exception {
    	BatchTextFileCopyST.main(new String [] { "-i", "/tmp/in", "-o", "/tmp/out" });  // mocked cmdline args
    }

    @Test
    public void testTmpMT() throws Exception {
    	BatchTextFileCopyMT.main(new String [] { "-i", "/tmp/in", "-o", "/tmp/out" });  // mocked cmdline args
    }

}
