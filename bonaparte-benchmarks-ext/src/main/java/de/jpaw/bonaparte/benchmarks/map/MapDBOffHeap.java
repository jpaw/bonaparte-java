package de.jpaw.bonaparte.benchmarks.map;

import java.util.Random;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*MapDBOffHeap.*"
//

//times measured with mapDB 1.0.2, JMH 0.7.3
//Benchmark                                         Mode   Samples         Mean   Mean error    Units
//d.j.b.b.m.MapDBBenchmark.readOnly                thrpt         9     1148.512       22.353   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeAnd4Reads          thrpt         9       11.684        0.584   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeNoCommit           thrpt         9       56.063        2.127   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeWithCommit         thrpt         9       12.387        0.545   ops/ms
//d.j.b.b.m.MapDBOffHeap.readOnly                  thrpt         9    35916.096      113.816   ops/ms
//d.j.b.b.m.MapDBOffHeap.writeAnd4Reads            thrpt         9      238.946        4.599   ops/ms
//d.j.b.b.m.MapDBOffHeap.writeNoCommit             thrpt         9      306.996       23.258   ops/ms
//d.j.b.b.m.MapDBOffHeap.writeWithCommit           thrpt         9      291.446        7.240   ops/ms

//times measured with mapDB 1.0.5, JMH 0.9.5
//# Run complete. Total time: 00:02:00
//
//Benchmark                                  Mode  Samples         Score  Score error  Units
//d.j.b.b.m.MapDBOffHeap.readOnly           thrpt        9  34946145.045  1173835.610  ops/s
//d.j.b.b.m.MapDBOffHeap.writeAnd4Reads     thrpt        9    238302.451     6590.540  ops/s
//d.j.b.b.m.MapDBOffHeap.writeNoCommit      thrpt        9    303739.030    10808.909  ops/s
//d.j.b.b.m.MapDBOffHeap.writeWithCommit    thrpt        9    296991.361     8876.687  ops/s

@State(value = Scope.Thread)
@OperationsPerInvocation(MapDBOffHeap.OPERATIONS_PER_INVOCATION)
public class MapDBOffHeap {
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
        db = DBMaker.newMemoryDirectDB()
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
