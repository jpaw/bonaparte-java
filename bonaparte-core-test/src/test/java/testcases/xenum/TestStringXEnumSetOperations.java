package testcases.xenum;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.testXenum.XAlphabet;

public class TestStringXEnumSetOperations {

    private void testUnify(String a, String b, String union) throws Exception {
        XAlphabet x = new XAlphabet(a);
        x.unifyWith(new XAlphabet(b));                 // Set union

        Assert.assertEquals(x, new XAlphabet(union));  // Set equals

        XAlphabet x1 = new XAlphabet(a);
        x1.unifyWith(b);                            // String arg union

        Assert.assertEquals(x1.getBitmap(), union); // String equals
    }

    @Test
    public void testEnumSetUnion() throws Exception {
        testUnify("AB", "BC", "ABC");
        testUnify("AB", "C",  "ABC");
        testUnify("AB", "B",  "AB");
        testUnify("AB", "A",  "AB");
        testUnify("BC", "AB", "ABC");
        testUnify("B",  "C",  "BC");
        testUnify("B",  "AB", "AB");
        testUnify("AB", "AB", "AB");
        testUnify("AB", "",   "AB");
        testUnify("",   "AB", "AB");
    }


    private void testIntersect(String a, String b, String common) throws Exception {
        XAlphabet x = new XAlphabet(a);
        x.intersectWith(new XAlphabet(b));             // Set Difference

        Assert.assertEquals(x, new XAlphabet(common)); // Set equals

        XAlphabet x1 = new XAlphabet(a);
        x1.intersectWith(b);                        // String arg Difference

        Assert.assertEquals(x1.getBitmap(), common);// String equals
    }

    @Test
    public void testEnumSetIntersect() throws Exception {
        testIntersect("AB", "BC", "B");
        testIntersect("AB", "C",  "");
        testIntersect("AB", "B",  "B");
        testIntersect("AB", "A",  "A");
        testIntersect("BC", "AB", "B");
        testIntersect("B",  "C",  "");
        testIntersect("B",  "AB", "B");
        testIntersect("AB", "AB", "AB");
        testIntersect("AB", "",   "");
        testIntersect("",   "AB", "");
    }


    private void testDifference(String a, String b, String difference) throws Exception {
        XAlphabet x = new XAlphabet(a);
        x.exclude(new XAlphabet(b));                       // Set exclusion

        Assert.assertEquals(x, new XAlphabet(difference)); // Set equals

        XAlphabet x1 = new XAlphabet(a);
        x1.exclude(b);                                  // String arg exclusion

        Assert.assertEquals(x1.getBitmap(), difference);// String equals
    }

    @Test
    public void testEnumSetDifference() throws Exception {
        testDifference("AB", "BC", "A");
        testDifference("AB", "C",  "AB");
        testDifference("AB", "B",  "A");
        testDifference("AB", "A",  "B");
        testDifference("BC", "AB", "C");
        testDifference("B",  "C",  "B");
        testDifference("B",  "AB", "");
        testDifference("AB", "AB", "");
        testDifference("AB", "",   "AB");
        testDifference("",   "AB", "");
        testDifference("ABC", "B", "AC");
        testDifference("ABC", "AC", "B");
    }


    private void testXor(String a, String b, String flipped) throws Exception {
        XAlphabet x = new XAlphabet(a);
        x.flip(new XAlphabet(b));                       // Set xor

        Assert.assertEquals(x, new XAlphabet(flipped)); // Set equals

        XAlphabet x1 = new XAlphabet(a);
        x1.flip(b);                                  // String arg xor

        Assert.assertEquals(x1.getBitmap(), flipped);// String equals
    }

    @Test
    public void testEnumSetXor() throws Exception {
        testXor("AB", "BC", "AC");
        testXor("AB", "C",  "ABC");
        testXor("AB", "B",  "A");
        testXor("AB", "A",  "B");
        testXor("BC", "AB", "AC");
        testXor("B",  "C",  "BC");
        testXor("B",  "AB", "A");
        testXor("AB", "AB", "");
        testXor("AB", "",   "AB");
        testXor("",   "AB", "AB");
        testXor("ABC", "B", "AC");
        testXor("ABC", "AC", "B");
    }


    private void testInit(String a, String result) throws Exception {
        XAlphabet x = new XAlphabet(a);
        Assert.assertEquals(x.getBitmap(), result);    // String equals
    }

    @Test
    public void testEnumSetInit() throws Exception {
        testInit("AB", "AB");
        testInit("",   "");
        testInit("BA", "AB");
        testInit("ACB", "ABC");
        testInit("CAB", "ABC");
        testInit("CBA", "ABC");
    }
}
