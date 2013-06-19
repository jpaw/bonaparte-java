package de.jpaw.money


import static extension de.jpaw.money.BonaMoneyOperators.*


/** Syntactic sugar for the BonaMoney class when used from xtend */
public class BonaMoneyOperatorDemo {
    def static void main(String [] args) {
        System::out.println("We got " + 17.83BD * "USD")

        val lines = new BonaMoney(new BonaCurrency('EUR'), false, true, 3BD, 1BD, 1BD, 1BD)
        System::out.println("1/3 of " + lines + " is " + lines * 0.3333334BD)
    }
}
