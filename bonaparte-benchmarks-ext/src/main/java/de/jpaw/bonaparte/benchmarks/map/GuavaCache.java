package de.jpaw.bonaparte.benchmarks.map;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

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

// Result:
// for GET, Map is about 4-8 times faster than Cache, depending on if timeouts are set.
// for PUT, Map is about 4-6 times faster, depending on if timeouts are set.


// timings ns:
// run results:
//Benchmark                Mode  Cnt    Score    Error  Units
//GuavaCache.chmGet        avgt    9  105.686 ± 11.172  ns/op
//GuavaCache.chmPut        avgt    9   43.938 ±  2.545  ns/op
//GuavaCache.guavaGetNoTO  avgt    9  373.119 ± 39.137  ns/op
//GuavaCache.guavaGetRdTO  avgt    9  859.240 ± 21.157  ns/op
//GuavaCache.guavaGetWrTO  avgt    9  573.457 ± 10.610  ns/op
//GuavaCache.guavaPutNoTO  avgt    9  120.493 ± 12.440  ns/op
//GuavaCache.guavaPutRdTO  avgt    9  183.040 ± 10.048  ns/op
//GuavaCache.guavaPutWrTO  avgt    9  176.098 ± 11.642  ns/op
//GuavaCache.hmGet         avgt    9   93.889 ± 10.426  ns/op
//GuavaCache.hmPut         avgt    9   22.735 ±  8.615  ns/op

// net result: put = 1:1, get = (measured - put) / 4
//
// hm          =  18 /  23
// chm         =  15 /  44
// guava no TO =  62 / 120
// guava rd TO = 170 / 180
// guava wr TO = 100 / 180

// => expiry timeouts add a significant overhead. Guava is a lot slower than Java hashMap anyway
// => for read operations, chm is not slower than hm (within measurement precision) 


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@OperationsPerInvocation(GuavaCache.OPERATIONS_PER_INVOCATION)
public class GuavaCache {
    static public final int OPERATIONS_PER_INVOCATION = 100000;
    
    public final Integer [] numbers = new Integer[OPERATIONS_PER_INVOCATION];
    public final Integer [] id = new Integer[OPERATIONS_PER_INVOCATION];
    public Cache<Integer,Integer> cacheNoTO;
    public Cache<Integer,Integer> cacheWrTO;
    public Cache<Integer,Integer> cacheRdTO;
    final Map<Integer,Integer> chm = new ConcurrentHashMap<Integer,Integer>(2 * OPERATIONS_PER_INVOCATION);
    final Map<Integer,Integer> hm = new HashMap<Integer,Integer>(2 * OPERATIONS_PER_INVOCATION);
    
    @Setup
    public void init() {
        Random rnd = new Random(2846284628L);
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            numbers[i] = Integer.valueOf(rnd.nextInt());
            id[i] = Integer.valueOf(i);
        }
        cacheNoTO = CacheBuilder
                .newBuilder()
                .build();
        cacheWrTO = CacheBuilder
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
        cacheRdTO = CacheBuilder
                .newBuilder()
                .expireAfterAccess(60, TimeUnit.SECONDS).build();
    }
    
    private void fillCache(Cache<Integer,Integer> cache) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            cache.put(numbers[i], id[i]);
    }
    
    private void readCache(Cache<Integer,Integer> cache, Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            bh.consume(cache.getIfPresent(numbers[i]));
    }

    private void fillMap(Map<Integer,Integer> cache) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            cache.put(numbers[i], id[i]);
    }
    
    private void readMap(Map<Integer,Integer> cache, Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            bh.consume(cache.get(numbers[i]));
    }

    @Benchmark
    public void guavaPutNoTO(Blackhole bh) {
        fillCache(cacheNoTO);
    }
    @Benchmark
    public void guavaPutWrTO(Blackhole bh) {
        fillCache(cacheWrTO);
    }
    @Benchmark
    public void guavaPutRdTO(Blackhole bh) {
        fillCache(cacheRdTO);
    }
    @Benchmark
    public void chmPut(Blackhole bh) {
        fillMap(chm);
    }
    @Benchmark
    public void hmPut(Blackhole bh) {
        fillMap(hm);
    }

    @Benchmark
    public void guavaGetNoTO(Blackhole bh) {
        fillCache(cacheNoTO);
        readCache(cacheNoTO, bh);
        readCache(cacheNoTO, bh);
        readCache(cacheNoTO, bh);
        readCache(cacheNoTO, bh);
    }
    @Benchmark
    public void guavaGetWrTO(Blackhole bh) {
        fillCache(cacheWrTO);
        readCache(cacheWrTO, bh);
        readCache(cacheWrTO, bh);
        readCache(cacheWrTO, bh);
        readCache(cacheWrTO, bh);
    }
    @Benchmark
    public void guavaGetRdTO(Blackhole bh) {
        fillCache(cacheRdTO);
        readCache(cacheRdTO, bh);
        readCache(cacheRdTO, bh);
        readCache(cacheRdTO, bh);
        readCache(cacheRdTO, bh);
    }
    @Benchmark
    public void chmGet(Blackhole bh) {
        fillMap(chm);
        readMap(chm, bh);
        readMap(chm, bh);
        readMap(chm, bh);
        readMap(chm, bh);
    }
    @Benchmark
    public void hmGet(Blackhole bh) {
        fillMap(hm);
        readMap(hm, bh);
        readMap(hm, bh);
        readMap(hm, bh);
        readMap(hm, bh);
    }
}
