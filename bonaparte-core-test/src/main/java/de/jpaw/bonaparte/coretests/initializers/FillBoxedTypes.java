package de.jpaw.bonaparte.coretests.initializers;

import de.jpaw.bonaparte.pojos.tests1.BoxedTypes;

public class FillBoxedTypes {
	static public BoxedTypes test1() {
		BoxedTypes x = new BoxedTypes();
		x.setByte1((byte) 13);
		x.setShort1((short) 42);
		x.setBoolean1(true);
		x.setChar1('Ä');
		x.setChar2('\n');
		x.setDouble1(42.5d);
		x.setFloat1(5.0e17f);
		x.setInt1(4242);
		x.setInt2(42424242);
		x.setLong1(424242424242424242L);
		return x;
	}
}
