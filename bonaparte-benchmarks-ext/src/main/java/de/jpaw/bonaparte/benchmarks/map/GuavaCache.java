package de.jpaw.bonaparte.benchmarks.map;


import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.logic.BlackHole;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

// Benchmarks to investigate how much performance impact setting an access or write timeout has to guava cache.

//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3
//Benchmark                                       Mode   Samples         Mean   Mean error    Units
//d.j.b.b.m.GuavaCache.mapGet                    thrpt         9    10160.454      208.834   ops/ms
//d.j.b.b.m.GuavaCache.guavaGetNoTO              thrpt         9     2691.573      240.131   ops/ms
//d.j.b.b.m.GuavaCache.guavaGetRdTO              thrpt         9     1182.600       30.350   ops/ms
//d.j.b.b.m.GuavaCache.guavaGetWrTO              thrpt         9     1630.863       39.137   ops/ms

//d.j.b.b.m.GuavaCache.mapPut                    thrpt         9    23428.648      949.636   ops/ms
//d.j.b.b.m.GuavaCache.guavaPutNoTO              thrpt         9     5963.303      206.986   ops/ms
//d.j.b.b.m.GuavaCache.guavaPutRdTO              thrpt         9     4005.642      227.445   ops/ms
//d.j.b.b.m.GuavaCache.guavaPutWrTO              thrpt         9     3969.242      229.006   ops/ms

//d.j.b.b.m.GuavaCache.javaNow                   thrpt         9     4539.995      365.355   ops/ms
//d.j.b.b.m.GuavaCache.jodaNew                   thrpt         9    23662.616      522.202   ops/ms
//d.j.b.b.m.GuavaCache.jodaNow                   thrpt         9    23762.011      301.599   ops/ms

// Result:
// for GET, Map is about 4-8 times faster than Cache, depending on if timeouts are set.
// for PUT, Map is about 4-6 times faster, depending on if timeouts are set.

@State(value = Scope.Thread)
@OperationsPerInvocation(GuavaCache.OPERATIONS_PER_INVOCATION)
public class GuavaCache {
    static public final int OPERATIONS_PER_INVOCATION = 100000;
    
    static public final Integer [] numbers = new Integer[OPERATIONS_PER_INVOCATION];
    static {
        Random rnd = new Random(2846284628L);
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            numbers[i] = rnd.nextInt();
    }
    
    private void fillCache(Cache<Integer,Integer> cache) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            cache.put(numbers[i], i);
    }
    
    private void readCache(Cache<Integer,Integer> cache, BlackHole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            bh.consume(cache.getIfPresent(numbers[i]));
    }

    private void fillMap(Map<Integer,Integer> cache) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            cache.put(numbers[i], i);
    }
    
    private void readMap(Map<Integer,Integer> cache, BlackHole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            bh.consume(cache.get(numbers[i]));
    }

//    
//  Benchmarks to measure the overhead to get a timestamp, for Joda and Java8 time 
//    
    
    @GenerateMicroBenchmark
    public void jodaNow(BlackHole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(org.joda.time.LocalDateTime.now());
        }
    }

    // requires Java 8 support
//    @GenerateMicroBenchmark
//    public void javaNow(BlackHole bh) {
//        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
//            bh.consume(java.time.LocalDateTime.now());
//        }
//    }

    @GenerateMicroBenchmark
    public void jodaNew(BlackHole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(new org.joda.time.LocalDateTime());
        }
    }

    // does not exist
//    @GenerateMicroBenchmark
//    public void javaNew(BlackHole bh) {
//        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
//            bh.consume(new java.time.LocalDateTime());
//        }
//    }

    @GenerateMicroBenchmark
    public void guavaPutNoTO(BlackHole bh) {
        final Cache<Integer,Integer> cache = CacheBuilder
                .newBuilder()
                .build();
        fillCache(cache);
    }
    @GenerateMicroBenchmark
    public void guavaPutWrTO(BlackHole bh) {
        final Cache<Integer,Integer> cache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
        fillCache(cache);
    }
    @GenerateMicroBenchmark
    public void guavaPutRdTO(BlackHole bh) {
        final Cache<Integer,Integer> cache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(60, TimeUnit.SECONDS).build();
        fillCache(cache);
    }
    @GenerateMicroBenchmark
    public void mapPut(BlackHole bh) {
        final Map<Integer,Integer> cache = new ConcurrentHashMap<Integer,Integer>(2 * OPERATIONS_PER_INVOCATION);
        fillMap(cache);
    }

    @GenerateMicroBenchmark
    public void guavaGetNoTO(BlackHole bh) {
        final Cache<Integer,Integer> cache = CacheBuilder
                .newBuilder()
                .build();
        fillCache(cache);
        readCache(cache, bh);
        readCache(cache, bh);
        readCache(cache, bh);
        readCache(cache, bh);
    }
    @GenerateMicroBenchmark
    public void guavaGetWrTO(BlackHole bh) {
        final Cache<Integer,Integer> cache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
        fillCache(cache);
        readCache(cache, bh);
        readCache(cache, bh);
        readCache(cache, bh);
        readCache(cache, bh);
    }
    @GenerateMicroBenchmark
    public void guavaGetRdTO(BlackHole bh) {
        final Cache<Integer,Integer> cache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(60, TimeUnit.SECONDS).build();
        fillCache(cache);
        readCache(cache, bh);
        readCache(cache, bh);
        readCache(cache, bh);
        readCache(cache, bh);
    }
    @GenerateMicroBenchmark
    public void mapGet(BlackHole bh) {
        final Map<Integer,Integer> cache = new ConcurrentHashMap<Integer,Integer>(2 * OPERATIONS_PER_INVOCATION);
        fillMap(cache);
        readMap(cache, bh);
        readMap(cache, bh);
        readMap(cache, bh);
        readMap(cache, bh);
    }
}
