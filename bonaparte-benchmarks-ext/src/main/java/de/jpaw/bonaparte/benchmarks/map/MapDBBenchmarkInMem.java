package de.jpaw.bonaparte.benchmarks.map;

import java.util.Random;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.TimeUnit;

import org.mapdb.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;


//Benchmark                            Mode  Cnt     Score    Error  Units
//MapDBBenchmarkInMem.readOnly         avgt    9    55.294 ±  1.479  ns/op
//MapDBBenchmarkInMem.writeAnd4Reads   avgt    9  1795.072 ± 64.726  ns/op
//MapDBBenchmarkInMem.writeNoCommit    avgt    9   824.227 ± 81.521  ns/op
//MapDBBenchmarkInMem.writeWithCommit  avgt    9   847.648 ± 16.343  ns/op

// => 800 ns per write, 150 per read (single Integer!)

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@OperationsPerInvocation(MapDBBenchmarkInMem.OPERATIONS_PER_INVOCATION)
public class MapDBBenchmarkInMem {
    static public final int OPERATIONS_PER_INVOCATION = 10000;
    static public final String DATA = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet";
    static public final Long KEY = 437L;

    private DB db;
    ConcurrentNavigableMap<Integer,Integer> map;
    
    static public final Integer [] numbers = new Integer[OPERATIONS_PER_INVOCATION];
    static {
        Random rnd = new Random(2846284628L);
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            numbers[i] = rnd.nextInt();
    }
    
    private void fillCache() {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            map.put(numbers[i], i);
    }
    
    private void readCache(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            bh.consume(map.get(numbers[i]));
    }
    
    @Setup
    public void setUp() {
        // configure and open database using builder pattern.
        // all options are available with code auto-completion.
        db = DBMaker.newHeapDB()
                   .closeOnJvmShutdown()
                   .make();

        // open existing an collection (or create new)
        map = db.getTreeMap("jmhBench");
        db.commit();  //persist changes into disk
    }
    
    @TearDown
    public void tearDown() {
        db.close();
    }

    @Benchmark
    public void writeNoCommit(Blackhole bh) {
        fillCache();
    }
    
    @Benchmark
    public void writeWithCommit(Blackhole bh) {
        fillCache();
        db.commit();
    }
    
    @Benchmark
    public void writeAnd4Reads(Blackhole bh) {
        fillCache();
        db.commit();
        readCache(bh);
        readCache(bh);
        readCache(bh);
        readCache(bh);
    }
    
    @Benchmark
    public void readOnly(Blackhole bh) {
        readCache(bh);
    }
}
