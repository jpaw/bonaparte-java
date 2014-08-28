package de.jpaw.fixedpoint;

public class FixedPointExtensions {

    static public FixedPointBase sum(Iterable<FixedPointBase> iterable) {
        FixedPointBase sum = Units.ZERO;
        for (FixedPointBase a : iterable) {
            sum = sum.add(a);
        }
        return sum;
    }

// commented methods sit within class itself now
//    static public FixedPointBase operator_plus(FixedPointBase a, FixedPointBase b) {
//        return a.add(b);
//    }
//    static public FixedPointBase operator_minus(FixedPointBase a, FixedPointBase b) {
//        return a.subtract(b);
//    }
//    static public FixedPointBase operator_multiply(FixedPointBase a, int b) {
//        return a.multiply(b);
//    }
    
    // != and == would make sense here if left null should be supported
//    static public boolean operator_equals(FixedPointBase a, FixedPointBase b) {
//        if (a == null)
//            return b == null;
//        else
//            return a.equals(b);
//    }
//    static public boolean operator_notEquals(FixedPointBase a, FixedPointBase b) {
//        if (a == null)
//            return b != null;
//        else
//            return !a.equals(b);
//    }
    
    static public Units units(long a) {
        return Units.of(a);
    }

    static public MilliUnits millis(long a) {
        return MilliUnits.of(a);
    }

    static public MicroUnits micros(long a) {
        return MicroUnits.of(a);
    }
    
    static public NanoUnits nanos(long a) {
        return NanoUnits.of(a);
    }

    static public VariableUnits ofScale(long a, int scale) {
        return new VariableUnits(a, scale);
    }

}