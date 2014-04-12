package de.jpaw.bonaparte.sak;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.pojos.sakTest.PropertyDefinition;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteBuilder;

public class EncodeBase64Bon {
    
    private static void output(ByteArrayComposer bac, String what) {
        ByteBuilder bb = new ByteBuilder();
        Base64.encodeToByte(bb, bac.getBuffer(), 0, bac.getLength());
        System.out.println("The encoded form of " + what + " is " + bb.toString());
    }
    
    private static void encode(BonaPortable obj, String what) {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
        output(bac, "OBJ(" + what + ")");
        bac.reset();
        bac.writeRecord(obj);
        output(bac, "RECORD(" + what + ")");
    }
    
    public static void main(String [] args) {
        encode(null, "null");
        encode(new PropertyDefinition("hello", "world"), "PropertyDefinition");
        encode(PropertyDefinition.class$MetaData(), "MetaData");
    }
}
