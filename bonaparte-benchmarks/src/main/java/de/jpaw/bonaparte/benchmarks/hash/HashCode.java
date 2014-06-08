package de.jpaw.bonaparte.benchmarks.hash;


import java.io.IOException;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.logic.BlackHole;

import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.Multiplicity;


@State(value = Scope.Thread)
@OperationsPerInvocation(HashCode.OPERATIONS_PER_INVOCATION)
public class HashCode {
    static public final int OPERATIONS_PER_INVOCATION = 1000000;

    @GenerateMicroBenchmark
    public void hashCodeBonaPortable(BlackHole bh) throws IOException {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(obj1.hashCode());
        }
    }

    @GenerateMicroBenchmark
    public void hashCodeBonaPortableClass(BlackHole bh) throws IOException {
        BonaPortableClass<ClassDefinition> obj1 = ClassDefinition.BClass.INSTANCE;
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(obj1.hashCode());
        }
    }

    @GenerateMicroBenchmark
    public void hashCodeMyEnum(BlackHole bh) throws IOException {
        Multiplicity m = Multiplicity.ARRAY;
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(m.hashCode());
        }
    }
    
    @GenerateMicroBenchmark
    public void hashCodeJavaEnum(BlackHole bh) throws IOException {
        java.lang.annotation.ElementType m = java.lang.annotation.ElementType.TYPE;
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(m.hashCode());
        }
    }
}
