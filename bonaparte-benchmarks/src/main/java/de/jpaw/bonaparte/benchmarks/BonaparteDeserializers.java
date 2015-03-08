package de.jpaw.bonaparte.benchmarks;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.ui.Alignment;
import de.jpaw.bonaparte.pojos.ui.LayoutHint;
import de.jpaw.bonaparte.pojos.ui.UIColumn;


//with OLD factory, using reflection
//Benchmark                                         Mode   Samples         Mean   Mean error    Units
//d.j.b.b.BonaparteDeserializers.deserByteArray    thrpt        30     1770.966       24.187   ops/ms


// with NEW factory, avoiding reflection
//Benchmark                                         Mode   Samples         Mean   Mean error    Units
//d.j.b.b.BonaparteDeserializers.deserByteArray    thrpt        30     1776.352       16.221   ops/ms
//d.j.b.b.BonaparteDeserializers.deserByteArray    thrpt        30     1762.030       32.048   ops/ms
// despite what the Hazelcast guys say, I see no difference....


@State(value = Scope.Thread)
@OperationsPerInvocation(BonaparteSerializers.OPERATIONS_PER_INVOCATION)
public class BonaparteDeserializers {
    static public final int OPERATIONS_PER_INVOCATION = 10000;

//  @Benchmark
//  public void deserCompact() throws IOException {
//        ClassDefinition obj1 = ClassDefinition.class$MetaData();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
//        DataOutputStream dataOut = new DataOutputStream(baos);
//        CompactComposer cc = new CompactComposer(dataOut, false);
//      for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
//          cc.reset();
//          cc.writeRecord(obj1);
//      }
//  }
//

    @Benchmark
    public void deserByteArray(Blackhole bh) throws IOException, MessageParserException {
        UIColumn obj1 = new UIColumn();
        obj1.setFieldName("Hello");
        obj1.setAlignment(Alignment.CENTER);
        obj1.setLayoutHint(LayoutHint.TEXT);
        obj1.setWidth(42);

        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(obj1);
        byte [] data = bac.getBytes();

        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            ByteArrayParser dst = new ByteArrayParser(data, 0, -1);
            bh.consume(dst.readRecord());
        }
    }


//  @Benchmark
//  public void deserStringBuilder() throws IOException {
//        ClassDefinition obj1 = ClassDefinition.class$MetaData();
//        StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder());
//      for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
//          sbc.reset();
//          sbc.writeRecord(obj1);
//      }
//  }

}
