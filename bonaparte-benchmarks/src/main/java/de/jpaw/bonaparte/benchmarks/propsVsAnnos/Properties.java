package de.jpaw.bonaparte.benchmarks.propsVsAnnos;

import java.lang.reflect.Field;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.proptest.ClassWithProperties;

//Benchmarks to investigate if the bonaparte properties are really worth it or if we should just use standard Java annotations

//java -jar target/bonaparte-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3
//# Run complete. Total time: 00:02:08
//
//Benchmark                                                   Mode   Samples         Mean   Mean error    Units
//d.j.b.b.propsVsAnnos.Properties.getClassAnnotationValue    thrpt         9    31035.833      978.143   ops/ms
//d.j.b.b.propsVsAnnos.Properties.getClassPropertyValue      thrpt         9   237147.954     9583.001   ops/ms
//d.j.b.b.propsVsAnnos.Properties.getFieldAnnotationValue    thrpt         9      197.387        5.528   ops/ms
//d.j.b.b.propsVsAnnos.Properties.getFieldPropertyValue      thrpt         9    19634.948     1260.742   ops/ms

// This benchmark suggests it is! Data on class level is retrieved 7.5 times faster, field values are retrieved about 100 times faster,
// most likely caused by the security checks which are done within reflection


@State(value = Scope.Thread)
@OperationsPerInvocation(Properties.OPERATIONS_PER_INVOCATION)
public class Properties {
    static public final int OPERATIONS_PER_INVOCATION = 100000;

    //
    //
    // Java Annotations / reflections tests
    //
    //

    private String getClassAnnotation(Object x) throws Exception {
        BenchAnnoClass anno = x.getClass().getAnnotation(BenchAnnoClass.class);
        return anno == null ? null : anno.value();
    }

    @Benchmark
    public void getClassAnnotationValue(Blackhole bh) throws Exception {
        Object obj = new ClassWithAnnotations();

        assert("Hello, class".equals(getClassAnnotation(obj)));

        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(getClassAnnotation(obj));
        }
    }


    private String getFieldAnnotation(Object x) throws Exception {
        Field f = x.getClass().getField("myField");
        if (f == null)
            return null;
        BenchAnnoField anno = f.getAnnotation(BenchAnnoField.class);
        return anno == null ? null : anno.value();
    }

    @Benchmark
    public void getFieldAnnotationValue(Blackhole bh) throws Exception {
        Object obj = new ClassWithAnnotations();

        assert("Hello, field".equals(getFieldAnnotation(obj)));

        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(getFieldAnnotation(obj));
        }
    }


    //
    //
    // Bonaparte property tests
    //
    //

    private String getClassProperty(BonaPortable obj) throws Exception {
        return obj.get$BonaPortableClass().getProperty("myClassProp");
    }

    @Benchmark
    public void getClassPropertyValue(Blackhole bh) throws Exception {
        BonaPortable obj = new ClassWithProperties();

        assert("Hello, class".equals(getClassProperty(obj)));

        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(getClassProperty(obj));
        }
    }


    private String getFieldProperty(BonaPortable obj) throws Exception {
        return obj.get$BonaPortableClass().getProperty("myField.myFieldProp");
    }

    @Benchmark
    public void getFieldPropertyValue(Blackhole bh) throws Exception {
        BonaPortable obj = new ClassWithProperties();

        assert("Hello, field".equals(getFieldProperty(obj)));

        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            bh.consume(getFieldProperty(obj));
        }
    }

}
