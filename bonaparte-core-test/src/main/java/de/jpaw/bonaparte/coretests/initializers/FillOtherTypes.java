package de.jpaw.bonaparte.coretests.initializers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.bonaparte.pojos.tests1.OtherTypes;
import de.jpaw.bonaparte.pojos.tests1.color;
import de.jpaw.util.ByteArray;
import de.jpaw.util.DayTime;

public class FillOtherTypes {
	static byte [] shortraw = new byte [] { 1, 2, 13, 0, 127, -8, -33, 99, 42 };
	
	static public OtherTypes test1() {
		OtherTypes x = new OtherTypes();
		x.setAscii1("Hello, world!");
		x.setColor1(color.GREEN);
		x.setDay1(new LocalDate(2012, 8, 5));
		x.setDecimal1(new BigDecimal("-3.14"));
		x.setNumber1(42);
		x.setRaw1(shortraw);
		x.setBinary1(new ByteArray(shortraw));
		x.setTimestamp1(new LocalDateTime(2012, 8, 5, 11, 55, 03));
		x.setTimestamp2(new LocalDateTime());
		x.setTimestamp3(new GregorianCalendar(2012, 8, 5, 11, 55, 03));
		x.setTimestamp4(DayTime.getCurrentTimestamp());
		x.setUnicode1("Hällo Wörld!\r\n");
		x.setMyUuid(UUID.randomUUID());
		return x;
	}
	
	static public OtherTypes test2(int size) {  // test with a long raw() in order to verify line breaks of base64 encoding
		OtherTypes x = test1();
		byte [] longraw = new byte [size];
		Arrays.fill(longraw, (byte)-33);
		x.setRaw1(longraw);
		x.setBinary1(new ByteArray(longraw));
		return x;
	}
}
