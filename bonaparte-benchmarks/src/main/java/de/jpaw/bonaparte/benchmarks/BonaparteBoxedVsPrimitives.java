package de.jpaw.bonaparte.benchmarks;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.pojos.myTypes.BoxedTypes;
import de.jpaw.bonaparte.pojos.myTypes.Primitives;
import de.jpaw.util.ByteBuilder;

//java -jar target/bonaparte-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*BonaparteBoxedVsPrimitives.*"

//Benchmark                                              Mode  Cnt     Score    Error  Units
//BonaparteBoxedVsPrimitives.parseAsciiBoxed             avgt   25  1294.524 ± 15.381  ns/op
//BonaparteBoxedVsPrimitives.parseAsciiPrimitives        avgt   25  1286.523 ± 19.071  ns/op
//BonaparteBoxedVsPrimitives.parseCompactBoxed           avgt   25   358.953 ± 18.963  ns/op
//BonaparteBoxedVsPrimitives.parseCompactPrimitives      avgt   25   361.237 ± 26.154  ns/op
//BonaparteBoxedVsPrimitives.serializeAsciiBoxed         avgt   25   978.158 ± 19.816  ns/op
//BonaparteBoxedVsPrimitives.serializeAsciiPrimitives    avgt   25   949.250 ±  9.515  ns/op
//BonaparteBoxedVsPrimitives.serializeCompactBoxed       avgt   25   365.635 ±  7.468  ns/op
//BonaparteBoxedVsPrimitives.serializeCompactPrimitives  avgt   25   355.135 ±  2.708  ns/op

// => Ascii is much slower than compact (as expected), no significant diff between primitives and boxed so far

// post opt:
//Benchmark                                          Mode  Cnt     Score    Error  Units
//BonaparteBoxedVsPrimitives.parseAsciiBoxed         avgt   25  1339.751 ± 83.107  ns/op
//BonaparteBoxedVsPrimitives.parseAsciiPrimitives    avgt   25  1465.184 ± 58.262  ns/op
//BonaparteBoxedVsPrimitives.parseCompactBoxed       avgt   25   353.063 ± 16.210  ns/op
//BonaparteBoxedVsPrimitives.parseCompactPrimitives  avgt   25   275.049 ± 10.373  ns/op

// => Ascii got slower ???? compact shows expected result.

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class BonaparteBoxedVsPrimitives {
    public static Primitives primObj = new Primitives((byte)88, (short)-23001, 827462846, 9278439287429437L, true, (float) 3.14, 2.718281828, 'X');
    public static BoxedTypes boxedObj = new BoxedTypes((byte)88, (short)-23001, 827462846, 9278439287429437L, true, (float) 3.14, 2.718281828, 'X');
    public static ByteBuilder builder = new ByteBuilder(2000, StandardCharsets.UTF_8);
    public static CompactByteArrayComposer cbac = new CompactByteArrayComposer(builder, true);
    public static ByteArrayComposer bac = new ByteArrayComposer();
    public static byte [] dataCompactPrimitives;
    public static byte [] dataAsciiPrimitives;
    public static byte [] dataCompactBoxed;
    public static byte [] dataAsciiBoxed;
    
    @Setup
    public void init() {
        // create premanufactured data output
        cbac.addField(Primitives.meta$$this, primObj);
        dataCompactPrimitives = builder.getBytes();
        cbac.reset();
        
        cbac.addField(BoxedTypes.meta$$this, boxedObj);
        dataCompactBoxed = builder.getBytes();
        cbac.reset();
        
        bac.addField(Primitives.meta$$this, primObj);
        dataAsciiPrimitives = bac.getBytes();
        bac.reset();
        
        bac.addField(BoxedTypes.meta$$this, boxedObj);
        dataAsciiBoxed = bac.getBytes();
        bac.reset();
    }
    
    @Benchmark
    @Threads(1)
    public ByteBuilder serializeCompactPrimitives() {
        cbac.reset();
        cbac.addField(Primitives.meta$$this, primObj);
        return cbac.getBuilder();
    }

    @Benchmark
    @Threads(1)
    public ByteBuilder serializeCompactBoxed() {
        cbac.reset();
        cbac.addField(BoxedTypes.meta$$this, boxedObj);
        return cbac.getBuilder();
    }
    
    @Benchmark
    @Threads(1)
    public byte [] serializeAsciiPrimitives() {
        bac.reset();
        bac.addField(Primitives.meta$$this, primObj);
        return bac.getBuffer();
    }

    @Benchmark
    @Threads(1)
    public byte [] serializeAsciiBoxed() {
        bac.reset();
        bac.addField(BoxedTypes.meta$$this, boxedObj);
        return bac.getBuffer();
    }
    
    @Benchmark
    @Threads(1)
    public Primitives parseCompactPrimitives() throws MessageParserException {
        CompactByteArrayParser bap = new CompactByteArrayParser(dataCompactPrimitives, 0, -1);
        return bap.readObject(Primitives.meta$$this, Primitives.class);
    }

    @Benchmark
    @Threads(1)
    public BoxedTypes parseCompactBoxed() throws MessageParserException {
        CompactByteArrayParser bap = new CompactByteArrayParser(dataCompactBoxed, 0, -1);
        return bap.readObject(BoxedTypes.meta$$this, BoxedTypes.class);
    }
    
    @Benchmark
    @Threads(1)
    public Primitives parseAsciiPrimitives() throws MessageParserException {
        ByteArrayParser bap = new ByteArrayParser(dataAsciiPrimitives, 0, -1);
        return bap.readObject(Primitives.meta$$this, Primitives.class);
    }

    @Benchmark
    @Threads(1)
    public BoxedTypes parseAsciiBoxed() throws MessageParserException {
        ByteArrayParser bap = new ByteArrayParser(dataAsciiBoxed, 0, -1);
        return bap.readObject(BoxedTypes.meta$$this, BoxedTypes.class);
    }
}
