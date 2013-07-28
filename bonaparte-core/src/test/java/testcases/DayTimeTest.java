package testcases;

import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import de.jpaw.util.DayTime;

public class DayTimeTest {
	@Test
	public void testTimeDifference() throws Exception {
		LocalDateTime start = new LocalDateTime();
		
		Thread.sleep(155L);
		LocalDateTime end = LocalDateTime.now();
		int diff = DayTime.LocalDateTimeDifference(start, end);
		System.out.println("Measured " + diff + " ms for an intended sleep time of 155 ms");
		assert diff >= 0L : "TimeDifference should return a positive value";
		assert diff <= 1000L : "TimeDifference should not return more than a second difference";
	}
}
