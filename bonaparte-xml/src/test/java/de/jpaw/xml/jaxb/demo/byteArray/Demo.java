package de.jpaw.xml.jaxb.demo.byteArray;
 
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.joda.time.LocalDate;
import de.jpaw.util.ByteArray;


public class Demo {

    public static void main(String[] args) throws Exception {
        ByteArrayMappers root = new ByteArrayMappers();
        root.localDate = new LocalDate(2011, 5, 30);
        root.array1 = "Hello, world".getBytes();
        root.array2 = new ByteArray("Hello, world".getBytes());
        JAXBContext jc = JAXBContext.newInstance(ByteArrayMappers.class);
 
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(root, System.out);
    }
}
