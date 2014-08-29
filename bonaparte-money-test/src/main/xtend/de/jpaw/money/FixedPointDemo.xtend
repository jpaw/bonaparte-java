package de.jpaw.money


import static extension de.jpaw.fixedpoint.FixedPointExtensions.*
import de.jpaw.fixedpoint.MilliUnits

/** Syntactic sugar for the BonaMoney class when used from xtend */
public class FixedPointOperatorDemo {
    def static void main(String [] args) {
        val myMillis = 130.millis
        val myMicros = 130L.micros
        val myNanos = 12.nanos
        val myNanos2 = 12L.nanos
        
        println("We got " + myMicros + " USD")

        val c = myMicros + myMillis
        val d = - myNanos
        val e = myMicros <=> myNanos
        
        println("c is " + c)
        println("d is " + d)
        println("e is " + e)
        
        val z = myMicros / 3
        val m = myMicros % 3
        
        println("result of " + myMicros + " / 3 is " + z + " remainder " + m)
        
        
        val vat = 19.units.percent
        val amount = MilliUnits.of(100.0BD)
        println("amount = " + amount + ", vat ratio is " + vat)
    }
}