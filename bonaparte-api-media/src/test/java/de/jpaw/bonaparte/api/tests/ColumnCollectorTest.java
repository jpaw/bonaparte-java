package de.jpaw.bonaparte.api.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.api.ColumnCollector;
import de.jpaw.bonaparte.pojos.ui.UIColumn;

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
        Assertions.assertEquals(totalWidth, 490);
    }
}
