package de.jpaw.xml.jaxb.demo.joda;
 
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;


public class Demo {

    public static void main(String[] args) throws Exception {
        DateTimeZone.setDefault(DateTimeZone.UTC); // for the constructors to work as expected.
        
        JodaTimeMappers root = new JodaTimeMappers();
        root.setDateTime(new DateTime(2011, 5, 30, 11, 2, 30, 0));
        root.setLocalDate(new LocalDate(2011, 5, 30));
        root.setLocalTime(new LocalTime(11, 2, 30));
        root.setLocalDateTime(new LocalDateTime(2011, 5, 30, 11, 2, 30));
 
        JAXBContext jc = JAXBContext.newInstance(JodaTimeMappers.class);
 
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(root, System.out);
    }
}
