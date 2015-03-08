package de.jpaw.bonaparte.benchmarks.logger;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import net.openhft.chronicle.tools.ChronicleTools;


//Benchmark                                  Mode   Samples         Mean   Mean error    Units
//d.j.b.b.l.DisruptorBench.processEvents    thrpt         9      211.231       50.014   ops/ms      // with UUID
//d.j.b.b.l.DisruptorBench.processEvents    thrpt         9     9890.041     1872.445   ops/ms      // with fixed text

@State(value = Scope.Thread)
@OperationsPerInvocation(DisruptorBench.OPERATIONS_PER_INVOCATION)
public class DisruptorBench {
    static public final int OPERATIONS_PER_INVOCATION = 4000;
    static public final String DATA = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet";
    static public final byte [] BYTES = DATA.getBytes();
    static public final String basePath = System.getProperty("java.io.tmpdir") + File.separator + "jmhBenchChronicle";
    static {
        ChronicleTools.deleteOnExit(basePath);
    }

    private static class MyElement {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public final static EventFactory<MyElement> EVENT_FACTORY = new EventFactory<MyElement>() {
            @Override
            public MyElement newInstance() {
                return new MyElement();
            }
        };
    }
    private Disruptor<MyElement> disruptor;
    private ExecutorService exec;
    private RingBuffer<MyElement> ringBuffer;

    @Setup
    public void setUp() throws IOException {
        exec = Executors.newCachedThreadPool();
        disruptor = new Disruptor<MyElement>(MyElement.EVENT_FACTORY, 4096, exec);
        final EventHandler<MyElement> handler = new EventHandler<DisruptorBench.MyElement>() {
            @Override
            public void onEvent(final MyElement event, final long sequenceNo, final boolean end) throws Exception {
                ;  // nuffin
            }
        };
        disruptor.handleEventsWith(handler);
        ringBuffer = disruptor.start();
    }

    @TearDown
    public void tearDown() throws IOException {
        disruptor.shutdown();
        exec.shutdown();
    }

    @Benchmark
    public void processEvents(Blackhole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            String myData = DATA; // UUID.randomUUID().toString();
            long seq = ringBuffer.next();
            MyElement xxx = ringBuffer.get(seq);
            xxx.setValue(myData);
            ringBuffer.publish(seq);
        }
    }
}
