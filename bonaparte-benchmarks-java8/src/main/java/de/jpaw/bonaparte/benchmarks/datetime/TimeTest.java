package de.jpaw.bonaparte.benchmarks.datetime;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

// Benchmarks to investigate how much performance of getting the current time

//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3
//# Run complete. Total time: 00:03:31
//
//Benchmark                             Mode   Samples         Mean   Mean error    Units
//d.j.b.b.d.TimeTest.javaNow           thrpt         9     4597.654       77.281   ops/ms
//d.j.b.b.d.TimeTest.javaNowInstant    thrpt         9    24120.503      296.265   ops/ms
//d.j.b.b.d.TimeTest.javaNowUTC        thrpt         9     9657.600      217.563   ops/ms
//d.j.b.b.d.TimeTest.javaSystem        thrpt         9    31357.708       97.409   ops/ms
//d.j.b.b.d.TimeTest.jodaNew           thrpt         9    23697.521      301.957   ops/ms
//d.j.b.b.d.TimeTest.jodaNow           thrpt         9    23539.440      166.600   ops/ms
//d.j.b.b.d.TimeTest.jodaNowInstant    thrpt         9    28859.156      334.488   ops/ms
//d.j.b.b.d.TimeTest.jodaNowUTC        thrpt         9    27503.306      496.864   ops/ms



@State(value = Scope.Thread)
@OperationsPerInvocation(TimeTest.OPERATIONS_PER_INVOCATION)
public class TimeTest {
    static public final int OPERATIONS_PER_INVOCATION = 100000;
    

//    
//  Benchmarks to measure the overhead to get a timestamp, for Joda and Java8 time 
//    
    
    @Benchmark
    public void javaSystem(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(System.currentTimeMillis());
        }
    }

    @Benchmark
    public void jodaNow(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(org.joda.time.LocalDateTime.now());
        }
    }

    @Benchmark
    public void jodaNowUTC(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(org.joda.time.LocalDateTime.now(org.joda.time.DateTimeZone.UTC));
        }
    }

    @Benchmark
    public void jodaNowInstant(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(org.joda.time.Instant.now());
        }
    }

    
    
    
    // requires Java 8 support
    @Benchmark
    public void javaNow(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(java.time.LocalDateTime.now());
        }
    }
    
    // requires Java 8 support
    @Benchmark
    public void javaNowUTC(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(java.time.LocalDateTime.now(java.time.ZoneId.of("Z")));
        }
    }
    
    // requires Java 8 support
    @Benchmark
    public void javaNowInstant(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(java.time.Instant.now());
        }
    }
    
    
    
    

    @Benchmark
    public void jodaNew(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(new org.joda.time.LocalDateTime());
        }
    }

    // does not exist
//    @Benchmark
//    public void javaNew(Blackhole bh) {
//        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
//            bh.consume(new java.time.LocalDateTime());
//        }
//    }
}
