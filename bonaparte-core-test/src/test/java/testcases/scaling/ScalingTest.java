package testcases.scaling;

import java.math.BigDecimal;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.scalingTest.Account;
import de.jpaw.bonaparte.pojos.scalingTest.Amounts;
import de.jpaw.bonaparte.pojos.scalingTest.Order;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.bonaparte.util.ToStringHelper;

public class ScalingTest {

    private Amounts multiply(BigDecimal quantity, Amounts unit) {
        Amounts result = new Amounts();
        result.gross = quantity.multiply(unit.gross);
        result.net = quantity.multiply(unit.net);
        result.tax = quantity.multiply(unit.tax);
        return result;
    }
    
    @Test
    public void bidirectionalConversion() {
        Order o = new Order();
        o.account = new Account("EUR");
        o.quantity = new BigDecimal("12.2");
        o.description = "some wine";
        o.unitPrice = new Amounts(new BigDecimal("123.2"), new BigDecimal("100"), new BigDecimal("23.2"));
        o.total = multiply(o.quantity, o.unitPrice);
        
        System.out.println("original order is " + ToStringHelper.toStringML(o));
        
        BigDecimal net = BigDecimalTools.retrieveScaled(o, "total.net"); 
        System.out.println("total net for EUR is " + net);
        BigDecimal netUnit = BigDecimalTools.retrieveScaled(o, "unitPrice.net"); 
        System.out.println("total net for EUR is " + netUnit);
        
        // change the currency
        o.account.currency = "JPY";
        net = BigDecimalTools.retrieveScaled(o, "total.net"); 
        System.out.println("total net for JPY is " + net);
        netUnit = BigDecimalTools.retrieveScaled(o, "unitPrice.net"); 
        System.out.println("total net for JPY is " + netUnit);
    }
}
