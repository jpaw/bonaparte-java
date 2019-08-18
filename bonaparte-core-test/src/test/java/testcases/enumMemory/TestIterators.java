package testcases.enumMemory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.enumtest.AlphabetEnum;

// tests memory allocation per method to iterate enums
//Memory used for iteration for oldSchool is 48
//Memory used for iteration for newGenerated is 48
//Memory used for iteration for usual is 120048 -- due to defensive array copy in Enum.values()

public class TestIterators {
    private static final int NUM_ITERATIONS = 1000;

    private final StringBuffer buff = new StringBuffer(100000);
    private long memSize;
    private ThreadMXBean tmb;
            
    private long getMemory() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        tmb = ManagementFactory.getThreadMXBean();
        com.sun.management.ThreadMXBean sb = (com.sun.management.ThreadMXBean)tmb;
        long threadId = Thread.currentThread().getId();
        return sb.getThreadAllocatedBytes(threadId);
    }
    
    private void before() {
        System.gc();
        memSize = getMemory();
    }
    private long after(String which) {
        long memUsed = getMemory() - memSize;
        System.out.println("Memory used for iteration for " + which + " is " + memUsed);
        return memUsed;
    }


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
    
    public static void main(String [] args) {
        TestIterators me = new TestIterators();
        // first iteration - ignore, preloading
        me.testOldSchool();
        me.testNewGenerated();
        me.testUsual();
        // second iteration - the relevant result 
        me.testOldSchool();
        me.testNewGenerated();
        me.testUsual();
        // third iteration - should be the same result 
        me.testOldSchool();
        me.testNewGenerated();
        me.testUsual();
    }
}
