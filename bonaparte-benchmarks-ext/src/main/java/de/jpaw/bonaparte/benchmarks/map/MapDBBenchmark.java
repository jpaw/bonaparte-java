package de.jpaw.bonaparte.benchmarks.map;

import java.io.File;
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

//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*MapDBBenchmark.*"

//times measured with mapDB 1.0.2, JMH 0.7.3
//# Run complete. Total time: 00:02:05
//
//Benchmark                                         Mode   Samples         Mean   Mean error    Units
//d.j.b.b.m.MapDBBenchmark.writeAnd4Reads          thrpt         9       11.726        0.361   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeNoCommit           thrpt         9       56.192        0.747   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeWithCommit         thrpt         9       12.155        1.001   ops/ms
//d.j.b.b.m.MapDBBenchmark.readOnly                thrpt         9     1117.955       40.204   ops/ms
//d.j.b.b.m.MapDBBenchmarkInMem.readOnly           thrpt         9    35438.079     1133.119   ops/ms
//d.j.b.b.m.MapDBBenchmarkInMem.writeAnd4Reads     thrpt         9      605.763       14.571   ops/ms
//d.j.b.b.m.MapDBBenchmarkInMem.writeNoCommit      thrpt         9     1299.891       88.656   ops/ms
//d.j.b.b.m.MapDBBenchmarkInMem.writeWithCommit    thrpt         9     1259.141       12.151   ops/ms

// times measured with mapDB 1.0.5, JMH 0.9.5
//# Run complete. Total time: 00:04:37
//
//Benchmark                                         Mode  Samples         Score  Score error  Units
//d.j.b.b.m.MapDBBenchmark.readOnly                thrpt        9   1095522.398    57786.307  ops/s
//d.j.b.b.m.MapDBBenchmark.writeAnd4Reads          thrpt        9     11868.852      489.263  ops/s
//d.j.b.b.m.MapDBBenchmark.writeNoCommit           thrpt        9     55638.444     2506.094  ops/s
//d.j.b.b.m.MapDBBenchmark.writeWithCommit         thrpt        9     12596.647      778.475  ops/s
//d.j.b.b.m.MapDBBenchmarkInMem.readOnly           thrpt        9  34887747.574  1465959.791  ops/s
//d.j.b.b.m.MapDBBenchmarkInMem.writeAnd4Reads     thrpt        9    608804.703    12155.537  ops/s
//d.j.b.b.m.MapDBBenchmarkInMem.writeNoCommit      thrpt        9   1319239.961    19852.000  ops/s
//d.j.b.b.m.MapDBBenchmarkInMem.writeWithCommit    thrpt        9   1241955.221    35134.413  ops/s

@State(value = Scope.Thread)
@OperationsPerInvocation(MapDBBenchmark.OPERATIONS_PER_INVOCATION)
public class MapDBBenchmark {
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
        db = DBMaker.newFileDB(new File("testdb"))
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
