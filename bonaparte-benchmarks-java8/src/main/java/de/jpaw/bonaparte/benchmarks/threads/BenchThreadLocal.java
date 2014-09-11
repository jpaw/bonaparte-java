package de.jpaw.bonaparte.benchmarks.threads;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

// Benchmarks to investigate how much performance the new lambda take

//java -jar target/bonaparte-benchmarks-java8.jar -i 3 -f 3 -wf 1 -wi 3 ".*Thread.*"
//
//Benchmark                                     Mode  Samples    Score  Score error  Units
//d.j.b.b.t.BenchThreadLocal.javaAtomic         avgt        9  530.012        1.923  ns/op
//d.j.b.b.t.BenchThreadLocal.javaPrimitive      avgt        9  170.118        2.325  ns/op
//d.j.b.b.t.BenchThreadLocal.javaThreadLocal    avgt        9  924.757        2.544  ns/op
//d.j.b.b.t.BenchThreadLocal.javaWrapper        avgt        9  385.112       28.798  ns/op



@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class BenchThreadLocal {
    public static final int NUM = 100;
    
    private int n = 0;
    private Integer nn = 0;
    private AtomicInteger nnn = new AtomicInteger();
    private ThreadLocal<Integer> n4 = new ThreadLocal<Integer>();
    
    @Benchmark
    public void javaPrimitive(Blackhole bh) {
        for (int i = 0; i < NUM; ++i) {
            ++n;
            bh.consume(n * i);
        }
    }
    
    @Benchmark
    public void javaWrapper(Blackhole bh) {
        for (int i = 0; i < NUM; ++i) {
            ++nn;
            bh.consume(nn * i);
        }
    }
    
    @Benchmark
    public void javaAtomic(Blackhole bh) {
        for (int i = 0; i < NUM; ++i) {
            int z = nnn.incrementAndGet();
            bh.consume(z * i);
        }
    }
    
    @Benchmark
    public void javaThreadLocal(Blackhole bh) {
        for (int i = 0; i < NUM; ++i) {
            n4.set(i);
            bh.consume(n4.get() * i);
        }
    }
}
