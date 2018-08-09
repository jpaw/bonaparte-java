package de.jpaw.bonaparte.benchmark.core;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.JsonComposerPrettyPrint;
import de.jpaw.bonaparte.pojos.meta.EnumDefinition;

public class JsonPretty {

    public static void main(String[] args) {
        BonaPortable obj = EnumDefinition.BClass.INSTANCE.getMetaData();
        System.out.println("Pretty EnumDefinition is " + JsonComposerPrettyPrint.toJsonString(obj));

        System.out.println("Old style EnumDefinition was " + JsonComposer.toJsonString(obj));
    }
}
