package de.jpaw.bonaparte.benchmarks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;

//Benchmark                                                Mode   Samples         Mean   Mean error    Units
//d.j.b.b.BonaparteSerializers.serByteArray               thrpt         9       46.713        6.920   ops/ms
//d.j.b.b.BonaparteSerializers.serByteArrayCompactId      thrpt         9      114.072        2.130   ops/ms
//d.j.b.b.BonaparteSerializers.serByteArrayCompactPqon    thrpt         9       78.844        2.918   ops/ms
//d.j.b.b.BonaparteSerializers.serCompactId               thrpt         9       71.300        0.833   ops/ms
//d.j.b.b.BonaparteSerializers.serCompactPqon             thrpt         9       41.204        2.296   ops/ms
//d.j.b.b.BonaparteSerializers.serKryoDefault             thrpt         9       51.745        0.493   ops/ms
//d.j.b.b.BonaparteSerializers.serStringBuilder           thrpt         9       66.861        1.561   ops/ms

@State(value = Scope.Thread)
@OperationsPerInvocation(BonaparteSerializers.OPERATIONS_PER_INVOCATION)
public class BonaparteSerializers {
	static public final int OPERATIONS_PER_INVOCATION = 10000;

    @GenerateMicroBenchmark
    public void serKryoDefault() throws IOException {
        Kryo kryo = new Kryo();
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        byte [] buffer = new byte[4000];
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            Output output = new Output(buffer);
            kryo.writeObject(output, obj1);
            output.close();
        }
    }
    
	@GenerateMicroBenchmark
	public void serCompactId() throws IOException {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
        DataOutputStream dataOut = new DataOutputStream(baos);
        CompactComposer cc = new CompactComposer(dataOut, true);
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
	        cc.reset();
	        cc.writeRecord(obj1);
		}
	}

    @GenerateMicroBenchmark
    public void serCompactPqon() throws IOException {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
        DataOutputStream dataOut = new DataOutputStream(baos);
        CompactComposer cc = new CompactComposer(dataOut, false);
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            cc.reset();
            cc.writeRecord(obj1);
        }
    }

    @GenerateMicroBenchmark
    public void serByteArrayCompactId() {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(4000, true);
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            cbac.reset();
            cbac.writeRecord(obj1);
        }
    }
    
    @GenerateMicroBenchmark
    public void serByteArrayCompactPqon() {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(4000, false);
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            cbac.reset();
            cbac.writeRecord(obj1);
        }
    }



	@GenerateMicroBenchmark
	public void serByteArray() throws IOException {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        ByteArrayComposer bac = new ByteArrayComposer();
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
	        bac.reset();
	        bac.writeRecord(obj1);
		}
	}


	@GenerateMicroBenchmark
	public void serStringBuilder() throws IOException {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder());
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
	        sbc.reset();
	        sbc.writeRecord(obj1);
		}
	}
}