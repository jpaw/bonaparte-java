package de.jpaw.bonaparte.benchmarks.propsVsAnnos;

@BenchAnnoClass("Hello, class")
public class ClassWithAnnotations {
    @BenchAnnoField("Hello, field")
    public int myField;
}
