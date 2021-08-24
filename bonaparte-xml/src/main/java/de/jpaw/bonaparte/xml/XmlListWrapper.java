package de.jpaw.bonaparte.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

// wrapper around java.util.list, because a marshalled list needs an XmlRootElement

@XmlRootElement(name="data")
public class XmlListWrapper {
    private final List<?> items;

    // not required, except for JAXB
    public XmlListWrapper() {
        this.items = new ArrayList();
    }

    public XmlListWrapper(List<?> items) {
        this.items = items;
    }

    @XmlAnyElement(lax=true)
    public List<?> getItems() {
        return items;
    }
}
