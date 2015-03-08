package de.jpaw.adapters.tests;

import java.math.BigDecimal;

import org.testng.annotations.Test;

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomAmountsUsed;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomAmountsUsed2;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomCurrency;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomMillis;
import de.jpaw.bonaparte.testrunner.MultiTestRunner;
import de.jpaw.bonaparte.testrunner.StringBuilderTestRunner;
import de.jpaw.fixedpoint.money.FPAmount;
import de.jpaw.fixedpoint.money.FPCurrency;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.util.StringSerializer;

public class TestFixedPoint {

    @Test
    public void testAdapterMilli() throws Exception {
        String expectedResult = StringSerializer.fromString(
                "\\R\\N\\Sadapters.tests.CustomMillis\\F\\Nhello\\F12500\\F7.889\\F\\O\\J").toString();
        CustomMillis myMillis = new CustomMillis("hello", MilliUnits.valueOf(12.5), MilliUnits.of(BigDecimal.valueOf(7889, 3)));

        System.out.println("Result is " + new StringBuilderTestRunner().serializationTest(myMillis, expectedResult));
        MultiTestRunner.serDeserMulti(myMillis, expectedResult);
    }

    @Test
    public void testAdapterCurrency() throws Exception {
        String expectedResult = StringSerializer.fromString(
                "\\R\\N\\Sadapters.tests.CustomCurrency\\F\\Nhello\\F\\Sadapters.moneyfp.FpCurrency\\F\\NEUR\\F2\\F\\O\\O\\J").toString();
        CustomCurrency myCurrency = new CustomCurrency("hello", new FPCurrency(JavaCurrencyDataProvider.instance.get("EUR")));

        System.out.println("serialized currency is " + new StringBuilderTestRunner().serializationTest(myCurrency, expectedResult));
        MultiTestRunner.serDeserMulti(myCurrency, expectedResult);
    }

    @Test
    public void testAdapterMilliNewString() throws Exception {
        String expectedResult = StringSerializer.altFromString(
                "<R><N><S>adapters.tests.CustomMillis<F><N>hello<F>12500<F>7.889<F><O>\n").toString();
        CustomMillis myMillis = new CustomMillis("hello", MilliUnits.valueOf(12.5), MilliUnits.of(BigDecimal.valueOf(7889, 3)));

        System.out.println("Result is " + new StringBuilderTestRunner().serializationTest(myMillis, expectedResult));
        MultiTestRunner.serDeserMulti(myMillis, expectedResult);
    }

    @Test
    public void testAdapterCurrencyNewString() throws Exception {
        String expectedResult = StringSerializer.altFromString(
                "<R><N><S>adapters.tests.CustomCurrency<F><N>hello<F><S>adapters.moneyfp.FpCurrency<F><N>EUR<F>2<F><O><O>\n").toString();
        CustomCurrency myCurrency = new CustomCurrency("hello", new FPCurrency(JavaCurrencyDataProvider.instance.get("EUR")));

        System.out.println("serialized currency is " + new StringBuilderTestRunner().serializationTest(myCurrency, expectedResult));
        MultiTestRunner.serDeserMulti(myCurrency, expectedResult);
    }

    @Test
    public void testAdapterCurrencyExt() throws Exception {
        FPCurrency stdEUR = new FPCurrency(JavaCurrencyDataProvider.instance.get("EUR"));
        FPCurrency microsEUR = stdEUR.withMicrosPrecision();
        long net = 1359000;
        long tax = net * 19 / 100;
        FPAmount units = new FPAmount(microsEUR, net + tax, net, tax);
        System.out.println("unit price is " + units);

        MicroUnits quantity = new MicroUnits(3000000);
        FPAmount total = units.convert(quantity, stdEUR);
        System.out.println("3 items cost " + total);

        CustomAmountsUsed item = new CustomAmountsUsed(stdEUR, units, quantity, total);
        String expectedResult = StringSerializer.altFromString(
                "<R><N><S>adapters.tests.CustomAmountsUsed<F><N>"
                + "EUR<F>"
                + "<S>adapters.moneyfp.FpAmountExt<F><N>1617210<F><B>2<F>1359000<F>258210<F><A><O>"
                + "3000000<F>"
                + "<S>adapters.moneyfp.FpAmountExt<F><N>485<F><B>2<F>408<F>77<F><A><O>"
                + "<O>\n");
        System.out.println("Result is " + new StringBuilderTestRunner().serializationTest(item, expectedResult));
        MultiTestRunner.serDeserMulti(item, expectedResult);
    }

    @Test
    public void testAdapterCurrencyExt2() throws Exception {
        FPCurrency stdEUR = new FPCurrency(JavaCurrencyDataProvider.instance.get("EUR"));
        FPCurrency microsEUR = stdEUR.withMicrosPrecision();
        long net = 1359000;
        long tax = net * 19 / 100;
        FPAmount units = new FPAmount(microsEUR, net + tax);
        System.out.println("unit price is " + units);

        MicroUnits quantity = new MicroUnits(3000000);
        FPAmount total = units.convert(quantity, stdEUR);
        System.out.println("3 items cost " + total);

        CustomAmountsUsed2 item = new CustomAmountsUsed2(stdEUR, units, quantity, total);
        String expectedResult = StringSerializer.altFromString(
                "<R><N><S>adapters.tests.CustomAmountsUsed2<F><N>"
                + "EUR<F>"
                + "1617210<F>"
                + "3000000<F>"
                + "485<F>"
                + "<O>\n");
        System.out.println("Result is " + new StringBuilderTestRunner().serializationTest(item, expectedResult));
        MultiTestRunner.serDeserMulti(item, expectedResult);
    }
}
