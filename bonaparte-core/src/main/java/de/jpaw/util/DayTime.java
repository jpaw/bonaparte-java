 /*
  * Copyright 2012 Michael Bischoff
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package de.jpaw.util;

import java.util.GregorianCalendar;
import java.util.Date;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Some convenience functions for days and timestamps. 
 * 
 * @author Michael Bischoff
 * 
 */
public class DayTime {

	/** Provides functionality missing in the {@link java.util.GregorianCalendar} class, to create a new object initialized with the current timestamp. */
	static public GregorianCalendar getCurrentTimestamp() {
		GregorianCalendar now = new GregorianCalendar();
		now.setTime(new Date());
		return now;  // TODO: set timezone to UTC as well?
	}
	
	/** Provides functionality to convert a Joda timestamp to a GregorianCalendar. */
	static public GregorianCalendar toGregorianCalendar(LocalDateTime when) {
		GregorianCalendar then = new GregorianCalendar();
		then.setTime(when.toDate());
		return then;  // TODO: set timezone to UTC as well?
	}
	
	/** Provides functionality to convert a Joda date to a GregorianCalendar. */
	static public GregorianCalendar toGregorianCalendar(LocalDate when) {
		GregorianCalendar then = new GregorianCalendar();
		then.setTime(when.toDate());
		return then;  // TODO: set timezone to UTC as well?
	}
}
