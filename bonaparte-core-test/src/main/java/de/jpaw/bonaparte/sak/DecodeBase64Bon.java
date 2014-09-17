package de.jpaw.bonaparte.sak;

import java.nio.charset.StandardCharsets;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.util.ToStringHelper;
import de.jpaw.util.Base64;

/** decode a base64 encoded serialized bonaparte class.
 * Please put JARs which contain relevant bonaparte classes into the classpath, as they are needed for decoding.
 * 
 * Sample invocation parameters:
 * object null: Dg==
 * meta.propertyDefinition as object: E21ldGEuUHJvcGVydHlEZWZpbml0aW9uBg5oZWxsbwZ3b3JsZAYQ
 * meta.propertyDefinition as record: Eg4TbWV0YS5Qcm9wZXJ0eURlZmluaXRpb24GDmhlbGxvBndvcmxkBhANCg==
 * 
 *  */
public class DecodeBase64Bon {

    public static void main(String [] args) {
        for (String s : args) {
            if (s != null && s.length() > 0) {
                String what = null;
                byte [] data = Base64.decodeFast(s.trim().getBytes(StandardCharsets.UTF_8));
                ByteArrayParser bap = new ByteArrayParser(data, 0, -1);
                try {
                    BonaPortable obj = null;
                    switch (data[0]) {
                    case 0x0e:
                    case 0x13:   // object or null
                        obj = bap.readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
                        what = "OBJ";
                        break;
                    case 0x12:   // record
                        obj = bap.readRecord();
                        what = "RECORD";
                        break;
                    default:
                        System.out.println(s + " represents an illegal sequence (initial byte is " + (int)data[0] + ")");
                    }
                    if (what != null) {
                        if (obj == null)
                            System.out.println(s + " represents null");
                        else
                            System.out.println(s + " represents " + what + "("+ ToStringHelper.toStringML(obj) + ")");
                    }
                } catch (Exception e) {
                    System.out.println(s + " cannot be parsed due to exception " + e.getMessage());
                }
            }
        }
    }
}
