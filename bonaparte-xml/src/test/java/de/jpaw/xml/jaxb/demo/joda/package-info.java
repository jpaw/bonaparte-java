/** A copy of this annotation is required in every package where the mappings should be used. */

@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type=DateTime.class,
        value=DateTimeAdapter.class),
    @XmlJavaTypeAdapter(type=LocalDate.class,
        value=LocalDateAdapter.class),
    @XmlJavaTypeAdapter(type=LocalTime.class,
        value=LocalTimeAdapter.class),
    @XmlJavaTypeAdapter(type=LocalDateTime.class,
        value=LocalDateTimeAdapter.class)
})
package de.jpaw.xml.jaxb.demo.joda;
 
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import de.jpaw.xml.jaxb.DateTimeAdapter;
import de.jpaw.xml.jaxb.LocalDateAdapter;
import de.jpaw.xml.jaxb.LocalTimeAdapter;
import de.jpaw.xml.jaxb.LocalDateTimeAdapter;

