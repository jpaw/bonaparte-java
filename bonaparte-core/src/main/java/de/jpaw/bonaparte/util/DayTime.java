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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * Some convenience functions for days and timestamps.
 * The conversion methods toCalendar and toDate are accessed from generated code.
 *
 * @author Michael Bischoff
 *
 */
public class DayTime {


    /** Provides functionality to convert a Java 8 LocalDateTime to a java Date. */
    static public Date toDate(LocalDateTime when) {
        if (when == null) {
            return null;
        }
        return new Date(86400_000L * when.toLocalDate().toEpochDay() + when.toLocalTime().toNanoOfDay() / 1000000L);
    }

    /** Provides functionality to convert a Java 8 LocalDate to a java Date. */
    static public Date toDate(LocalDate when) {
        if (when == null) {
            return null;
        }
        return new Date(86400_000L * when.toEpochDay());
    }

    /** Provides functionality to convert a Java 8 LocalTime to a java Date. */
    static public Date toDate(LocalTime when) {
        if (when == null) {
            return null;
        }
        return new Date(when.toNanoOfDay() / 1000000L);
    }

    /** Provides functionality to convert a Java 8 Instant to a java Date. */
    static public Date toDate(Instant when) {
        if (when == null) {
            return null;
        }
        return Date.from(when);
    }


    /** Converts the day portion of a LocalDate or localDateTime into a number in the format YYYYMMDD. */
    static public int dayAsInt(LocalDateTime when) {
        return when.getDayOfMonth() + 100 * when.getMonthValue() + 10000 * when.getYear();
    }
    /** Converts the day portion of a LocalDate or localDateTime into a number in the format YYYYMMDD. */
    static public int dayAsInt(LocalDate when) {
        return when.getDayOfMonth() + 100 * when.getMonthValue() + 10000 * when.getYear();
    }

    /** Converts the time portion of a LocalTime or localDateTime into a number in the format HHMMSSMMM. */
    static public int timeAsInt(LocalDateTime when) {
        return when.getNano() / 1000000 + 1000 * when.getSecond() + 100000 * when.getMinute() + 10000000 * when.getHour();
    }
    /** Converts the time portion of a LocalTime or localDateTime into a number in the format HHMMSSMMM. */
    static public int timeAsInt(LocalTime when) {
        return when.getNano() / 1000000 + 1000 * when.getSecond() + 100000 * when.getMinute() + 10000000 * when.getHour();
    }
    
    static public int millisOfDay(LocalTime t) {
        return (int)(t.toNanoOfDay() / 1000000L);
    }
    static public int millisOfDay(LocalDateTime t) {
        return (int)(t.toLocalTime().toNanoOfDay() / 1000000L);
    }
    static public long millisOfEpoch(Instant t) {
        return 1000L * t.getEpochSecond() + t.getNano() / 1000000;
    }
}
