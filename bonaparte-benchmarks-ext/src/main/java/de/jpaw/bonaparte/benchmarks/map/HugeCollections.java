package de.jpaw.bonaparte.benchmarks.map;


import java.util.Random;
import net.openhft.collections.HugeConfig;
import net.openhft.collections.HugeHashMap;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.logic.BlackHole;

// Benchmarks to investigate openHft HugeCollections performance

//Benchmark                                   Mode   Samples         Mean   Mean error    Units
//d.j.b.b.m.HugeCollections.write            thrpt         9       67.221        2.471   ops/ms
//d.j.b.b.m.HugeCollections.writeReadRead    thrpt         9       20.684        0.295   ops/ms


@State(value = Scope.Thread)
@OperationsPerInvocation(HugeCollections.OPERATIONS_PER_INVOCATION)
public class HugeCollections {
    static public final int OPERATIONS_PER_INVOCATION = 100000;
    
    static public final Integer [] numbers = new Integer[OPERATIONS_PER_INVOCATION];
    static {
        Random rnd = new Random(2846284628L);
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            numbers[i] = rnd.nextInt();
    }
    
    private final HugeConfig config = HugeConfig.DEFAULT.clone()
            .setSegments(128)
            .setSmallEntrySize(128)
            .setCapacity(OPERATIONS_PER_INVOCATION);
    private final HugeHashMap<Integer,Integer> map = new HugeHashMap<Integer, Integer>(
            config, Integer.class, Integer.class);
    
    @Setup
    public void setUp() {
    }
    
    @TearDown
    public void tearDown() {
    }

    
    private void fillCache() {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            map.put(numbers[i], i);
    }
    
    private void readCache(BlackHole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            bh.consume(map.get(numbers[i]));
    }


    @GenerateMicroBenchmark
    public void write(BlackHole bh) {
        fillCache();
    }
    
    @GenerateMicroBenchmark
    public void writeReadRead(BlackHole bh) {
        fillCache();
        readCache(bh);
        readCache(bh);
    }
}
