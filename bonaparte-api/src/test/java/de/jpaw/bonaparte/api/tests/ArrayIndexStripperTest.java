package de.jpaw.bonaparte.api.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.api.ColumnCollector;

public class ArrayIndexStripperTest {
    private final ColumnCollector cc = new ColumnCollector();

    private void check(String in, String expected, String testcase) throws Exception {
        String actual = cc.stripArrayIndexes(in);
        Assertions.assertEquals(actual, expected, testcase);
    }

    @Test
    public void checkIndexStripper() throws Exception {
        check("bla.di.bla", "bla.di.bla", "no index");
        check("bla[1].di.bla[4]", "bla.di.bla", "two indexes");
        check("bla[1][2][3].di[x].bla[4]", "bla.di.bla", "many indexes");
    }
}
