package de.jpaw.bonaparte.benchmarks.compression;

import java.io.UnsupportedEncodingException;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.logic.BlackHole;

import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;


//Benchmarks to evaluate compression / decompression speed with LZ4-java

//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*LZ4.*"

//ClassDef: Uncompressed length = 2563, compressed length = 1037
//String: Uncompressed length = 294, compressed length = 264

//Benchmark                                   Mode   Samples         Mean   Mean error    Units
//d.j.b.b.c.LZ4.compressClass                thrpt         9      199.573        3.544   ops/ms
//d.j.b.b.c.LZ4.compressString               thrpt         9      930.807       23.536   ops/ms
//d.j.b.b.c.LZ4.uncompressClass              thrpt         9      858.290        7.773   ops/ms
//d.j.b.b.c.LZ4.uncompressKnownSizeClass     thrpt         9      853.847       12.694   ops/ms
//d.j.b.b.c.LZ4.uncompressKnownSizeString    thrpt         9     6868.212      216.922   ops/ms
//d.j.b.b.c.LZ4.uncompressString             thrpt         9     7136.771       33.089   ops/ms

@State(value = Scope.Thread)
@OperationsPerInvocation(LZ4.OPERATIONS_PER_INVOCATION)
public class LZ4 {
    static public final int OPERATIONS_PER_INVOCATION = 100000;
    static public final String DATA = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet";

    private final LZ4Factory factory = LZ4Factory.fastestInstance();
    private final LZ4Compressor compressor = factory.fastCompressor();
    private final LZ4FastDecompressor fastDecompressor = factory.fastDecompressor();
    private final LZ4SafeDecompressor safeDecompressor = factory.safeDecompressor();
    
    private static class Testcase {
        private byte[] uncompressedForm = null;
        private byte[] compressedForm = null;
        private int compressedLength = -1;
        private int decompressedLength = -1;
        
        Testcase(byte [] data, String which) {
            uncompressedForm = data;
            decompressedLength = data.length;
            // compress data
            LZ4Factory factory = LZ4Factory.fastestInstance();
            LZ4Compressor compressor = factory.fastCompressor();
            final int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
            compressedForm = new byte[maxCompressedLength];
            compressedLength = compressor.compress(uncompressedForm, 0, decompressedLength, compressedForm, 0, maxCompressedLength);
            System.out.println(which + ": Uncompressed length = " + decompressedLength + ", compressed length = " + compressedLength);
            assert(compressedLength == compressedForm.length);
            
        }
    }
    
    private static Testcase cd;
    private static Testcase sd;
    
    private static ClassDefinition obj1 = ClassDefinition.class$MetaData();
    static {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.reset();
        bac.writeRecord(obj1);
        cd = new Testcase(bac.getBytes(), "ClassDef");
    }
        
        
    @Setup
    public void setUp() throws UnsupportedEncodingException {
        sd = new Testcase(DATA.getBytes("UTF-8"), "String");
    }
    
    @TearDown
    public void tearDown() {
    }

    private void runCompressCase(Testcase tc, BlackHole bh) {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            final int maxCompressedLength = compressor.maxCompressedLength(tc.decompressedLength);
            tc.compressedForm = new byte[maxCompressedLength];
            tc.compressedLength = compressor.compress(tc.uncompressedForm, 0, tc.decompressedLength, tc.compressedForm, 0, maxCompressedLength);
            bh.consume(tc.compressedForm);
        }
        
    }
    
    @GenerateMicroBenchmark
    public void compressString(BlackHole bh) {
        runCompressCase(sd, bh);
    }

    @GenerateMicroBenchmark
    public void compressClass(BlackHole bh) {
        runCompressCase(cd, bh);
    }

    
    private void runUncompressCase(Testcase tc, BlackHole bh) {
        byte [] target = new byte[tc.uncompressedForm.length];
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            int compressedLength2 = fastDecompressor.decompress(tc.compressedForm, 0, target, 0, tc.decompressedLength);
            bh.consume(compressedLength2);
        }
    }

    @GenerateMicroBenchmark
    public void uncompressString(BlackHole bh) {
        runUncompressCase(sd, bh);
    }

    @GenerateMicroBenchmark
    public void uncompressClass(BlackHole bh) {
        runUncompressCase(cd, bh);
    }
    
    
    private void runUncompressKnownSize(Testcase tc, BlackHole bh) {
        byte [] target = new byte[tc.uncompressedForm.length];
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            int compressedLength2 = safeDecompressor.decompress(tc.compressedForm, 0, tc.compressedLength, target, 0);
            bh.consume(compressedLength2);
        }
    }

    @GenerateMicroBenchmark
    public void uncompressKnownSizeString(BlackHole bh) {
        runUncompressKnownSize(sd, bh);
    }
    
    @GenerateMicroBenchmark
    public void uncompressKnownSizeClass(BlackHole bh) {
        runUncompressKnownSize(cd, bh);
    }
}
