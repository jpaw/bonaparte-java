package de.jpaw.bonaparte.api.tests;

import java.util.Collections;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.api.ColumnCollector;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.ui.UIColumnConfiguration;
import de.jpaw.bonaparte.util.ToStringHelper;

public class UIMapperTest {

    @Test
    public void uiMapTest() throws Exception {
        ColumnCollector cc = new ColumnCollector();

        for (FieldDefinition f: ClassDefinition.class$MetaData().getFields()) {
            System.out.println("UI meta for " + f.getName() + " is " + ToStringHelper.toStringML(cc.createMeta(f)));
        }
    }

    @Test
    public void uiMap2Test() throws Exception {
        ColumnCollector cc = new ColumnCollector();

        for (FieldDefinition f: UIColumnConfiguration.class$MetaData().getFields()) {
            System.out.println("UI meta for " + f.getName() + " is " + ToStringHelper.toStringML(cc.createMeta(f)));
        }
    }

    @Test
    public void uiMap3Test() throws Exception {
        ColumnCollector cc = new ColumnCollector();
        
        UIColumnConfiguration uic = new UIColumnConfiguration();
        uic.setFieldName("meta[8].enumInstances");

        cc.createUIMetas(Collections.singletonList(uic), UIColumnConfiguration.class$MetaData());
        System.out.println("UI meta for " + uic.getFieldName() + " is " + ToStringHelper.toStringML(uic));
    }
}
