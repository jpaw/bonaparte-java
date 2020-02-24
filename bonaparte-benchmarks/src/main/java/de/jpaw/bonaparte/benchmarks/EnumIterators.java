package de.jpaw.bonaparte.benchmarks;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import de.jpaw.bonaparte.pojos.enumtest.AlphabetEnum;

// results:
// java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*EnumIterators.*"

//Benchmark                   Mode  Cnt   Score   Error  Units
//EnumIterators.empty         avgt    9  46.237 ± 0.040  ns/op
//EnumIterators.oldSchool     avgt    9  68.261 ± 0.038  ns/op
//EnumIterators.usual         avgt    9  75.332 ± 0.192  ns/op
//EnumIterators.newGenerated  avgt    9  67.750 ± 0.139  ns/op

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class EnumIterators {

    @Benchmark
    public void empty(Blackhole bh) {
        for (int i = 0; i < 26; ++i) {
            bh.consume(i);
        }
    }

    @Benchmark
    public void oldSchool(Blackhole bh) {
        for (int i = 0; i < 26; ++i) {
            AlphabetEnum a = AlphabetEnum.valueOf(i);
            bh.consume(a.name());
        }
    }

    @Benchmark
    public void usual(Blackhole bh) {
        for (AlphabetEnum a: AlphabetEnum.values()) {
            bh.consume(a.name());
        }
    }

    @Benchmark
    public void newGenerated(Blackhole bh) {
        for (AlphabetEnum a: AlphabetEnum.all) {
            bh.consume(a.name());
        }
    }
}
