package de.jpaw.bonaparte.batch.tests;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.batch.AnalyzerCSVMain;

public class AnalzerIT {

    @Test
    public void testAnalyze() throws Exception {
        AnalyzerCSVMain.main(new String [] { "-i", "/tmp/analyzerIn.csv" });  // mocked cmdline args
    }


}
