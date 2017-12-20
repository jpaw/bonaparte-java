package de.jpaw.bonaparte.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

// results:

//Benchmark                                              Mode   Samples         Mean   Mean error    Units
//d.j.b.b.StringSerializers.getMaxCodeWithCharAt        thrpt        30    45492.676      387.264   ops/ms
//d.j.b.b.StringSerializers.getMaxCodeWithReflection    thrpt        30   187551.524      730.145   ops/ms

//Benchmark                                           Mode   Samples         Mean   Mean error    Units
//d.j.b.b.StringSerializers.copyNewArray             thrpt        30   113449.843     2347.386   ops/ms
//d.j.b.b.StringSerializers.copyToExistingArray      thrpt        30   105213.276      194.749   ops/ms
//d.j.b.b.StringSerializers.getPrivateCharAddress    thrpt        30   209207.793     1499.305   ops/ms


@State(value = Scope.Thread)
@OperationsPerInvocation(StringSerializers.OPERATIONS_PER_INVOCATION)
public class StringSerializers {
    static public final int OPERATIONS_PER_INVOCATION = 1000000;
    protected volatile String subject = "Hello, world";
//    private static Field unsafeString = calculateUnsafe();
//
//    static private Field calculateUnsafe() {
//        try {
//            Field f = String.class.getDeclaredField("value");
//            f.setAccessible(true);
//            return f;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @Benchmark
    public void empty() {
    }

//    @Benchmark
//    public void getPrivateCharAddress(Blackhole bh)
//            throws IllegalArgumentException, IllegalAccessException {
//        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
//            bh.consume(unsafeString.get(subject));
//    }

    @Benchmark
    public void copyNewArray(Blackhole bh) throws IllegalArgumentException,
            IllegalAccessException {
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i)
            bh.consume(subject.toCharArray());
    }

    @Benchmark
    public void copyToExistingArray(Blackhole bh)
            throws IllegalArgumentException, IllegalAccessException {
        char[] tmp = new char[100];
        for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
            subject.getChars(0, subject.length(), tmp, 0);
            bh.consume(tmp);
        }
    }

    @Benchmark
    public void getMaxCodeWithCharAt(Blackhole bh) {
        for (int k = 0; k < OPERATIONS_PER_INVOCATION; ++k) {
            int maxCode = 0;
            int len = subject.length();
            for (int i = 0; i < len; ++i) {
                int c = (int) subject.charAt(i);
                if (c > maxCode)
                    maxCode = c;
            }
            bh.consume(maxCode);
        }
    }

//    @Benchmark
//    public void getMaxCodeWithReflection(Blackhole bh)
//            throws IllegalArgumentException, IllegalAccessException {
//        for (int k = 0; k < OPERATIONS_PER_INVOCATION; ++k) {
//            char buff[] = (char[]) unsafeString.get(subject);
//            int maxCode = 0;
//            int len = buff.length;
//            for (int i = 0; i < len; ++i) {
//                if (buff[i] > maxCode)
//                    maxCode = buff[i];
//            }
//        }
//    }
}
