package de.jpaw.bonaparte.core.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.ui.UIColumn;
import de.jpaw.bonaparte.util.ColumnCollector;

public class ColumnCollectorTest {

    @Test
    public void testUIColumnCollector() throws Exception {
        ColumnCollector cc = new ColumnCollector();
        
        cc.addToColumns(UIColumn.class$MetaData());
        
        int totalWidth = 0;
        for (UIColumn c : cc.columns) {
            totalWidth += c.getWidth();
            System.out.println(c);
        }
        Assert.assertEquals(totalWidth, 490);
    }
}
