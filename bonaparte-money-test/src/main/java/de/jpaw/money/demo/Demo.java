package de.jpaw.money.demo;

import java.math.BigDecimal;

import de.jpaw.money.BonaCurrency;
import de.jpaw.money.BonaMoney;
import de.jpaw.money.MonetaryException;

public class Demo {

    /**
     * @param args
     * @throws MonetaryException
     */
    public static void main(String[] args) throws MonetaryException {
        BonaCurrency currency = new BonaCurrency("EUR");
        BonaMoney m = new BonaMoney(currency, false, new BigDecimal("3.14"));
        System.out.println("Single Amount: " + m.toString());

        BonaMoney mt = new BonaMoney(currency, false, true, null, new BigDecimal("100.14"), new BigDecimal("19.0"), new BigDecimal("7.0"));
        System.out.println("Taxed Amount: " + mt.toString());

    }

}
