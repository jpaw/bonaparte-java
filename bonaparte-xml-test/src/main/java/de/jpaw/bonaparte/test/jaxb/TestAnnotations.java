package de.jpaw.bonaparte.test.jaxb;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlRootElement;

import de.jpaw.bonaparte.pojos.test.jaxb.TestXml;

public class TestAnnotations {

    public static void main(String[] args) {
        Annotation x = TestXml.class.getAnnotation(XmlRootElement.class);
        if (x == null) {
            System.out.println("TestXml is not annotated with @XmlRootElement");
        } else {
            System.out.println("TestXml has been annotated with @XmlRootElement");
        }
        Annotation [] xx = TestXml.class.getAnnotations();
        System.out.println("TestXml has " + xx.length + " annotations");
    }
}
