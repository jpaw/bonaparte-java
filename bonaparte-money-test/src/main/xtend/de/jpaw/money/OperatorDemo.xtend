package de.jpaw.money


import static extension de.jpaw.money.BonaMoneyOperators.*


/** Syntactic sugar for the BonaMoney class when used from xtend */
public class BonaMoneyOperatorDemo {
    def static void main(String [] args) {
        System::out.println("We got " + 17.83BD * "USD")
    }
}
