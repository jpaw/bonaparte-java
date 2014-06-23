package de.jpaw.bonaparte.benchmarks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;

//Benchmark                                         Mode   Samples         Mean   Mean error    Units
//d.j.b.b.BonaparteSerializers.serByteArray        thrpt        30       45.215        0.522   ops/ms
//d.j.b.b.BonaparteSerializers.serCompact          thrpt        30       39.286        0.792   ops/ms
//d.j.b.b.BonaparteSerializers.serStringBuilder    thrpt        30       63.173        1.211   ops/ms

@State(value = Scope.Thread)
@OperationsPerInvocation(BonaparteSerializers.OPERATIONS_PER_INVOCATION)
public class BonaparteSerializers {
	static public final int OPERATIONS_PER_INVOCATION = 10000;

	@GenerateMicroBenchmark
	public void serCompact() throws IOException {
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
