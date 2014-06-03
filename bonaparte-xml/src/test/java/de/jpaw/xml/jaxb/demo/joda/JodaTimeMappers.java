package de.jpaw.xml.jaxb.demo.joda;

// courtesy of http://blog.bdoughan.com/2011/05/jaxb-and-joda-time-dates-and-times.html

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

@XmlRootElement
@XmlType(propOrder={
   "dateTime",
   "dateMidnight",
   "localDate",
   "localTime",
   "localDateTime"})

public class JodaTimeMappers {
    
    private DateTime dateTime;
    private LocalDate localDate;
    private LocalTime localTime;
    private LocalDateTime localDateTime;
 
    public DateTime getDateTime() {
        return dateTime;
    }
 
    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
 
    public LocalDate getLocalDate() {
        return localDate;
    }
 
    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
 
    public LocalTime getLocalTime() {
        return localTime;
    }
 
    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }
 
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
 
    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
 
}