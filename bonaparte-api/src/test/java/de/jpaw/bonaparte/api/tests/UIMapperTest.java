package de.jpaw.bonaparte.api.tests;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.api.ColumnCollector;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.util.ToStringHelper;

public class UIMapperTest {

    @Test
    public void uiMapTest() throws Exception {
        ColumnCollector cc = new ColumnCollector();

        for (FieldDefinition f: ClassDefinition.class$MetaData().getFields()) {
            System.out.println("UI meta for " + f.getName() + " is " + ToStringHelper.toStringML(cc.createMeta(f)));
        }
    }
}
