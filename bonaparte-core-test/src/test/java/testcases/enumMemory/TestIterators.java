package testcases.enumMemory;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.pojos.enumtest.AlphabetEnum;

public class TestIterators {
    private static final int NUM_ITERATIONS = 100;

    private final StringBuilder buff = new StringBuilder(100000);
    private long memSize;

    public void oldSchool() {
        for (int i = 0; i < 26; ++i) {
            AlphabetEnum a = AlphabetEnum.valueOf(i);
            buff.append(a.name());
        }
    }

    public void usual() {
        for (AlphabetEnum a: AlphabetEnum.values()) {
            buff.append(a.name());
        }
    }

    public void newGenerated() {
        for (AlphabetEnum a: AlphabetEnum.all) {
            buff.append(a.name());
        }
    }

    private void before() {
        System.gc();
        memSize = Runtime.getRuntime().freeMemory();
    }
    private long after(String which) {
        long memUsed = memSize - Runtime.getRuntime().freeMemory();
        System.out.println("Memory used for iteration for " + which + " is " + memUsed);
        return memUsed;
    }


    @Test
    public void testOldSchool() {
        oldSchool();  // load classes
        before();
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            oldSchool();
            buff.setLength(0);
        }
        after("oldSchool");
    }

    @Test
    public void testUsual() {
        usual();  // load classes
        before();
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            usual();
            buff.setLength(0);
        }
        after("usual");
    }

    @Test
    public void testNewGenerated() {
        newGenerated();  // load classes
        before();
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            newGenerated();
            buff.setLength(0);
        }
        after("newGenerated");
    }
}
