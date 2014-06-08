package de.jpaw.bonaparte.benchmarks.map;

import java.io.File;
import java.util.Random;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.*;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.logic.BlackHole;

//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*MapDBBenchmark.*"
//# Run complete. Total time: 00:02:05
//
//Benchmark                                    Mode   Samples         Mean   Mean error    Units
//d.j.b.b.m.MapDBBenchmark.writeAnd4Reads     thrpt         9       11.726        0.361   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeNoCommit      thrpt         9       56.192        0.747   ops/ms
//d.j.b.b.m.MapDBBenchmark.writeWithCommit    thrpt         9       12.155        1.001   ops/ms
//d.j.b.b.m.MapDBBenchmark.readOnly           thrpt         9     1117.955       40.204   ops/ms

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
    
    private void readCache(BlackHole bh) {
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

    @GenerateMicroBenchmark
    public void writeNoCommit(BlackHole bh) {
        fillCache();
    }
    
    @GenerateMicroBenchmark
    public void writeWithCommit(BlackHole bh) {
        fillCache();
        db.commit();
    }
    
    @GenerateMicroBenchmark
    public void writeAnd4Reads(BlackHole bh) {
        fillCache();
        db.commit();
        readCache(bh);
        readCache(bh);
        readCache(bh);
        readCache(bh);
    }
    
    @GenerateMicroBenchmark
    public void readOnly(BlackHole bh) {
        readCache(bh);
    }
}
