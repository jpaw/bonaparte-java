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

//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*MapDB.*"
//
//Benchmark                                         Mode   Samples         Mean   Mean error    Units
//d.j.b.b.m.MapDBBenchmark.readOnly                thrpt         9     1148.512       22.353   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeAnd4Reads          thrpt         9       11.684        0.584   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeNoCommit           thrpt         9       56.063        2.127   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeWithCommit         thrpt         9       12.387        0.545   ops/ms
//d.j.b.b.m.MapDBBenchmarkInMem.readOnly           thrpt         9    35438.079     1133.119   ops/ms
//d.j.b.b.m.MapDBBenchmarkInMem.writeAnd4Reads     thrpt         9      605.763       14.571   ops/ms
//d.j.b.b.m.MapDBBenchmarkInMem.writeNoCommit      thrpt         9     1299.891       88.656   ops/ms
//d.j.b.b.m.MapDBBenchmarkInMem.writeWithCommit    thrpt         9     1259.141       12.151   ops/ms
//d.j.b.b.m.MapDBOffHeap.readOnly                  thrpt         9    35916.096      113.816   ops/ms
//d.j.b.b.m.MapDBOffHeap.writeAnd4Reads            thrpt         9      238.946        4.599   ops/ms
//d.j.b.b.m.MapDBOffHeap.writeNoCommit             thrpt         9      306.996       23.258   ops/ms
//d.j.b.b.m.MapDBOffHeap.writeWithCommit           thrpt         9      291.446        7.240   ops/ms


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
