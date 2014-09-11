package de.jpaw.bonaparte.benchmarks.lambda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

// Benchmarks to investigate how much performance the new lambda take

//java -jar target/bonaparte-benchmarks-java8.jar -i 3 -f 3 -wf 1 -wi 3 ".*Lambda.*"
//# Run complete. Total time: 00:02:31
//
//Benchmark                                             Mode  Samples    Score  Score error  Units
//d.j.b.b.l.LambdaTest.javaClassicList                  avgt        9  333.962       19.718  ns/op
//d.j.b.b.l.LambdaTest.javaClassicPrimitive             avgt        9   84.046        0.278  ns/op
//d.j.b.b.l.LambdaTest.javaClassicWrapper               avgt        9  437.076        0.981  ns/op
//d.j.b.b.l.LambdaTest.javaLambda                       avgt        9  162.861        3.227  ns/op
//d.j.b.b.l.LambdaTest.javaLambdaParallel               avgt        9 3947.922      294.273  ns/op
//d.j.b.b.l.LambdaTest.javaLambdaListIterator           avgt        9  256.027        1.337  ns/op
//d.j.b.b.l.LambdaTest.javaLambdaListIteratorParallel   avgt        9 4367.844      173.519  ns/op



@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class LambdaTest {
    public static final int NUM = 100;
    
    private List<String> stringList;
    
    @Setup
    public void setUp() throws IOException {
        stringList = new ArrayList<String>(100);
        for (int i = 0; i < NUM; ++i) {
            stringList.add("Hello number " + i);
        }
    }

//    
//  Benchmarks to measure the overhead to run a loop, in standard and in lambda mode 
//    
    
    @Benchmark
    public void javaClassicPrimitive(Blackhole bh) {
        for (int i = 0; i < NUM; ++i) {
            bh.consume(i * i);
        }
    }

    @Benchmark
    public void javaClassicWrapper(Blackhole bh) {
        for (Integer i = 0; i < NUM; ++i) {
            bh.consume(i * i);
        }
    }

    @Benchmark
    public void javaLambda(Blackhole bh) {
        IntStream.range(0, NUM).forEach(i -> bh.consume(i * i));
    }

    @Benchmark
    public void javaClassicList(Blackhole bh) {
        for (String x: stringList) {
            bh.consume(x);
        }
    }

    @Benchmark
    public void javaLambdaListIterator(Blackhole bh) {
        stringList.forEach(x -> bh.consume(x));
    }

    @Benchmark
    public void javaLambdaParallel(Blackhole bh) {
        IntStream.range(0, NUM).parallel().forEach(i -> bh.consume(i * i));
    }

    @Benchmark
    public void javaLambdaListIteratorParallel(Blackhole bh) {
        stringList.parallelStream().forEach(x -> bh.consume(x));
    }

}
