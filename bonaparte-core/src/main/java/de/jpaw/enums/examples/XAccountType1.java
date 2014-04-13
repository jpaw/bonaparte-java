package de.jpaw.enums.examples;

import de.jpaw.enums.XEnumFactory;

/** Extendable enum instance type. This class has immutable instances. Also, after static initialization, no additional members can be created. */ 
public class XAccountType1 extends XAccountType0 {
	/////public static final int MAX_TOKEN_LENGTH = 1;
	public static final String PQON = "base.XAccountType1";
	public static final int NUM_VALUES_TOTAL = XAccountType0.NUM_VALUES_TOTAL + AccountType1.values().length;
	
	static {
		// create all the instances
		// create a factory instance
		AccountType1 [] values = AccountType1.values();
		for (int i = 0; i < values.length; ++i) {
			AccountType1 e = values[i];
			myFactory.publishInstance(new XAccountType1(e, i + XAccountType0.NUM_VALUES_TOTAL, e.name(), e.getToken(), myFactory));
		}
		myFactory.register(PQON, XAccountType1.class);
	}
	public static XAccountType0 valueOf(AccountType1 enumVal) {
		return myFactory.getByEnum(enumVal);
	}
	
	// constructor may not be accessible from the outside
	protected XAccountType1(Enum<?> enumVal, int ordinal, String name, String token, XEnumFactory<XAccountType0> myFactory) {
		super(enumVal, ordinal, name, token, myFactory);
	}
}
