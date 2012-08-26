package de.jpaw.bonaparte.coretests.initializers;

import java.math.BigDecimal;

import de.jpaw.bonaparte.pojos.tests1.NoRounding;

public class FillNoRounding {
	static public NoRounding test1() {
		NoRounding x = new NoRounding();
		BigDecimal [] elements = new BigDecimal [9];
		elements[0] = BigDecimal.valueOf(  -1L, 2);		// negative, sign in fraction only
		elements[1] = BigDecimal.valueOf( -97L, 2);		// negative, sign in fraction only
		elements[2] = BigDecimal.valueOf(-103L, 2);		// negative, sign in fraction and amount
		elements[3] = BigDecimal.valueOf(-589L, 2);		// negative, close to next
		elements[4] = BigDecimal.ZERO;
		elements[5] = BigDecimal.valueOf(   5L, 2);		// positive
		elements[6] = BigDecimal.valueOf(  59L, 2);		// positive
		elements[7] = BigDecimal.valueOf( 317L, 2);		// positive, round down potential
		elements[8] = BigDecimal.valueOf(5377L, 2);		// positive, round up potential
		x.setElements(elements);
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal e : elements)
			sum = sum.add(e);
		x.setSum(sum);
		return x;
	}
}
