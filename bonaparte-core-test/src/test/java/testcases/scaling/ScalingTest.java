package testcases.scaling;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.scalingTest.Account;
import de.jpaw.bonaparte.pojos.scalingTest.Amounts;
import de.jpaw.bonaparte.pojos.scalingTest.ListOfOrders;
import de.jpaw.bonaparte.pojos.scalingTest.Order;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.bonaparte.util.ToStringHelper;

public class ScalingTest {

    private Amounts multiply(BigDecimal quantity, Amounts unit) {
        Amounts result = new Amounts();
        result.gross = quantity.multiply(unit.gross);
        result.net = quantity.multiply(unit.net);
        result.tax = quantity.multiply(unit.tax);
        result.somethingElse = quantity.multiply(unit.somethingElse);
        return result;
    }

    @Test
    public void bidirectionalConversion() {
        System.out.println("simple paths:");

        Order o = new Order();
        o.account = new Account("TND");
        o.quantity = new BigDecimal("12.2");
        o.description = "some wine";
        o.unitPrice = new Amounts(new BigDecimal("123.2"), new BigDecimal("100"), new BigDecimal("23.2"), new BigDecimal("11.1"));
        o.total = multiply(o.quantity, o.unitPrice);

        System.out.println("original order is " + ToStringHelper.toStringML(o));

        BigDecimal net = BigDecimalTools.retrieveScaled(o, "total.net");
        System.out.println("total net for TND is " + net);
        Assert.assertEquals(net.scale(), 3);
        BigDecimal netUnit = BigDecimalTools.retrieveScaled(o, "unitPrice.net");
        System.out.println("unitprice net for TND is " + netUnit);
        Assert.assertEquals(netUnit.scale(), 4);
        BigDecimal se1 = BigDecimalTools.retrieveScaled(o, "total.somethingElse");
        System.out.println("total se for TND is " + se1);
        Assert.assertEquals(se1.scale(), 2);
        BigDecimal se2 = BigDecimalTools.retrieveScaled(o, "unitPrice.somethingElse");
        System.out.println("unitPrice se for TND is " + se2);
        Assert.assertEquals(se2.scale(), 2);

        // change the currency
        o.account.currency = "JPY";
        net = BigDecimalTools.retrieveScaled(o, "total.net");
        System.out.println("total net for JPY is " + net);
        Assert.assertEquals(net.scale(), 0);
        netUnit = BigDecimalTools.retrieveScaled(o, "unitPrice.net");
        System.out.println("unitprice net for JPY is " + netUnit);
        Assert.assertEquals(netUnit.scale(), 4);
        se1 = BigDecimalTools.retrieveScaled(o, "total.somethingElse");
        System.out.println("total se for JPY is " + se1);
        Assert.assertEquals(se1.scale(), 2);
        se2 = BigDecimalTools.retrieveScaled(o, "unitPrice.somethingElse");
        System.out.println("unitPrice se for JPY is " + se2);
        Assert.assertEquals(se2.scale(), 2);

        // now with a longer path
        System.out.println("longer paths:");

        ListOfOrders lof = new ListOfOrders();
        lof.setUnit("HQ");
        lof.setOrders(new ArrayList<Order>(6));
        lof.getOrders().add(o);

        o.account.currency = "TND";
        net = BigDecimalTools.retrieveScaled(lof, "orders[0].total.net");
        System.out.println("total net for TND is " + net);
        Assert.assertEquals(net.scale(), 3);

    }
}
