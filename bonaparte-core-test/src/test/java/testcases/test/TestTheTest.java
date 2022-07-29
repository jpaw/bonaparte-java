package testcases.test;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.testrunner.ByteArrayTestRunner;

public class TestTheTest {
    @Test
    public void testTheTestRunner() throws Exception {
        byte [] testData = "this cannot be parsed".getBytes();
        int iWantThisCode = 200017011;

        new ByteArrayTestRunner().expectDeserializationError(testData, iWantThisCode);
    }

    @Test
    public void testTheTestRunnerNegativeTest() throws Exception {
        byte [] testData = "this cannot be parsed".getBytes();
        int iWantThisCode = 200017012;

        try {
            new ByteArrayTestRunner().expectDeserializationError(testData, iWantThisCode);
        } catch (Exception e) {
            return;  // all OK, I wanted this
        }
        throw new Exception("Wanted some exception but did not get it");
    }
}
