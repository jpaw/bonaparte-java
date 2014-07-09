package de.jpaw.xml.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.joda.time.Instant;
 
public class InstantAdapter
    extends XmlAdapter<String, Instant>{
 
    public Instant unmarshal(String v) throws Exception {
        return new Instant(v);
    }
 
    public String marshal(Instant v) throws Exception {
        return v.toString();
    }
}