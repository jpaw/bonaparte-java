package de.jpaw.adapters.tests;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomAmountsUsed;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.money.FPAmount;
import de.jpaw.fixedpoint.money.FPCurrency;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.util.StringSerializer;

/** Test the output of externals as objects, compared to default marshalling. */
public class TestExternalObjectOutput {

    private static class ExternalStringBuilderComposer extends StringBuilderComposer {

        public ExternalStringBuilderComposer(StringBuilder work) {
            super(work);
        }

        @Override
        public boolean addExternal(ObjectReference di, Object obj) throws IOException {
            if (obj == null) {
                writeNull(di);
            } else {
                addRawData(obj.toString());
                terminateField();
            }
            return true;       // internal conversion done!
        }
    }
    @Test
    public void testAdapterMilli() throws Exception {
        FPCurrency curr = new FPCurrency(JavaCurrencyDataProvider.INSTANCE.get("EUR")).withDefaultPrecision();
        FPAmount unitPrice = new FPAmount(curr.withMicrosPrecision(), 3141593);
        MicroUnits quantity = MicroUnits.of(2500000L);

        CustomAmountsUsed t = new CustomAmountsUsed(
                curr,
                unitPrice,
                quantity,
                unitPrice.convert(quantity, curr));
        System.out.println("amounts is " + t);

        // test default output
        String defaultOut = StringBuilderComposer.marshal(StaticMeta.OUTER_BONAPORTABLE, t);
        String expectedResult = StringSerializer.altFromString(
                "<S>adapters.tests.CustomAmountsUsed<F><N>"
                + "EUR<F>"
                + "<S>adapters.moneyfp.FpAmountExt<F><N>3141593<F><N><O>"
                + "2500000<F>"
                + "<S>adapters.moneyfp.FpAmountExt<F><N>785<F><N><O>"
                + "<O>");
        Assert.assertEquals(defaultOut, expectedResult);


        // do the same with a customer serializer, taking objects
        StringBuilder buff = new StringBuilder();
        ExternalStringBuilderComposer composer = new ExternalStringBuilderComposer(buff);
        composer.addField(StaticMeta.OUTER_BONAPORTABLE, t);
        String newOutput = buff.toString();
        String newExpectedResult = StringSerializer.altFromString(
                "<S>adapters.tests.CustomAmountsUsed<F><N>"
                + "EUR<F>"
                + "3.141593 EUR:6<F>"
                + "2.5<F>"
                + "7.85 EUR<F>"
                + "<O>");
        Assert.assertEquals(newOutput, newExpectedResult);
    }
}
