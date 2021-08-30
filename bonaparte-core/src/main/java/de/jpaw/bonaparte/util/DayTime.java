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
package de.jpaw.bonaparte.util;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Some convenience functions for days and timestamps.
 * The conversion methods toCalendar and toDate are accessed from generated code.
 *
 * @author Michael Bischoff
 *
 */
public class DayTime {

	/** shorthand */
	static public int millisOfDay(LocalTime t) {
	    return t.toSecondOfDay() * 1000 + t.getNano() / 1000000;
	}

    /** Converts the day portion of a LocalDate or localDateTime into a number in the format YYYYMMDD. */
    static public int dayAsInt(LocalDate when) {
        return when.getDayOfMonth() + 100 * when.getMonthValue() + 10000 * when.getYear();
    }

    /** Converts the time since midnight (in milliseconds, i.e. 0..86_400_000) to a LocalTime. */
    static public LocalTime timeForMillis(int millis) {
        return LocalTime.ofNanoOfDay(millis * 1000000L);
    }
}
