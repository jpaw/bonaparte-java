package de.jpaw.bonaparte.benchmarks.hash;


import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.Multiplicity;


//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*HashCode.*"

//Benchmark                                        Mode   Samples         Mean   Mean error    Units
//d.j.b.b.h.HashCode.hashCodeBonaPortable         thrpt         9     1154.881       24.679   ops/ms    uncached
//d.j.b.b.h.HashCode.hashCodeBonaPortable         thrpt         9  1197227.839     3113.875   ops/ms    cached hash
//d.j.b.b.h.HashCode.hashCodeBonaPortableClass    thrpt         9   713069.623    24302.902   ops/ms
//d.j.b.b.h.HashCode.hashCodeJavaEnum             thrpt         9   718571.233     1931.601   ops/ms
//d.j.b.b.h.HashCode.hashCodeMyEnum               thrpt         9   718228.369     2079.186   ops/ms


@State(value = Scope.Thread)
@OperationsPerInvocation(HashCode.OPERATIONS_PER_INVOCATION)
public class HashCode {
    static public final int OPERATIONS_PER_INVOCATION = 1000000;

    @Benchmark
    public void hashCodeBonaPortable(Blackhole bh) throws IOException {
        ClassDefinition obj1 = ClassDefinition.class$MetaData();
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(obj1.hashCode());
        }
    }

    @Benchmark
    public void hashCodeBonaPortableClass(Blackhole bh) throws IOException {
        BonaPortableClass<ClassDefinition> obj1 = ClassDefinition.BClass.INSTANCE;
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(obj1.hashCode());
        }
    }

    @Benchmark
    public void hashCodeMyEnum(Blackhole bh) throws IOException {
        Multiplicity m = Multiplicity.ARRAY;
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(m.hashCode());
        }
    }

    @Benchmark
    public void hashCodeJavaEnum(Blackhole bh) throws IOException {
        java.lang.annotation.ElementType m = java.lang.annotation.ElementType.TYPE;
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(m.hashCode());
        }
    }
}
