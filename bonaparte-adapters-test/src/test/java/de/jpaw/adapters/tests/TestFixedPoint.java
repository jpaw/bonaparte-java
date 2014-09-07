package de.jpaw.adapters.tests;

import java.math.BigDecimal;

import org.testng.annotations.Test;

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomCurrency;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomMillis;
import de.jpaw.bonaparte.testrunner.MultiTestRunner;
import de.jpaw.bonaparte.testrunner.StringBuilderTestRunner;
import de.jpaw.fixedpoint.money.FPCurrency;
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
                "\\R\\N\\Sadapters.tests.CustomCurrency\\F\\Nhello\\F\\Sadapters.fpmoney.FpCurrency\\F\\NEUR\\F2\\F\\O\\O\\J").toString();
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
                "<R><N><S>adapters.tests.CustomCurrency<F><N>hello<F><S>adapters.fpmoney.FpCurrency<F><N>EUR<F>2<F><O><O>\n").toString();
        CustomCurrency myCurrency = new CustomCurrency("hello", new FPCurrency(JavaCurrencyDataProvider.instance.get("EUR")));
        
        System.out.println("serialized currency is " + new StringBuilderTestRunner().serializationTest(myCurrency, expectedResult));
        MultiTestRunner.serDeserMulti(myCurrency, expectedResult);
    }
}
