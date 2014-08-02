package de.jpaw.bonaparte.benchmarks.logger;

import java.io.File;
import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;
import net.openhft.chronicle.tools.ChronicleTools;

// results HUGEly depend on disk write activity 
//
//Result: 949.974 ±(99.9%) 22278.045 ops/ms
//Statistics: (min, avg, max) = (78.735, 949.974, 2345.738), stdev = 1221.135
//Confidence interval (99.9%): [-21328.071, 23228.018]
//
//
//# Run complete. Total time: 00:02:09
//
//Benchmark                                    Mode   Samples         Mean   Mean error    Units
//d.j.b.b.l.ChronicleBench.writeWithCommit    thrpt         9      727.728     1435.416   ops/ms

@State(value = Scope.Thread)
@OperationsPerInvocation(ChronicleBench.OPERATIONS_PER_INVOCATION)
public class ChronicleBench {
    static public final int OPERATIONS_PER_INVOCATION = 500000;
    static public final String DATA = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet";
    static public final byte [] BYTES = DATA.getBytes();
    static public final String basePath = System.getProperty("java.io.tmpdir") + File.separator + "jmhBenchChronicle";
    static {
        ChronicleTools.deleteOnExit(basePath);
    }

    private IndexedChronicle chronicle;
    private ExcerptAppender appender;
    
    @Setup
    public void setUp() throws IOException {
        chronicle = new IndexedChronicle(basePath);
        // chronicle.useUnsafe(true); // for benchmarks.
        // final Excerpt excerpt = chronicle.createExcerpt();
        appender = chronicle.createAppender();
    }
    
    @TearDown
    public void tearDown() throws IOException {
        chronicle.close();
    }
    
    @Benchmark
    public void writeWithCommit(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            appender.startExcerpt(4096);
            appender.write(BYTES);
            appender.finish();
        }
    }
}
