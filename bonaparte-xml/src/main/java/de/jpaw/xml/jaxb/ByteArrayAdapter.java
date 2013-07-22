package de.jpaw.xml.jaxb;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.jpaw.util.ByteArray;

/** Maps between the immutable object ByteArray and a byte[]. */

public class ByteArrayAdapter extends XmlAdapter<byte [], ByteArray> {
      
    @Override
    public ByteArray unmarshal(byte[] v) throws Exception {
        return new ByteArray(v);
    }

    @Override
    public byte[] marshal(ByteArray v) throws Exception {
        return v.getBytes();
    }
}
