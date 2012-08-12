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

/**
 * The DayTime class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Some convenience functions for days and timestamps. 
 */
public class DayTime {

	static public GregorianCalendar getCurrentTimestamp() {
		GregorianCalendar now = new GregorianCalendar();
		now.setTime(new Date());
		return now;  // TODO: set timezone to UTC as well?
	}
}
