package testcases.xenum;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.testXenum.ManyLetters;
import de.jpaw.enums.ImmutableStringEnumSet;
import de.jpaw.enums.TokenizableEnum;

public class TestImmutableEnumSet {
    public static final ImmutableStringEnumSet BASE = ImmutableStringEnumSet.of("BCDEF");

    private void testAdd(TokenizableEnum b, String union) throws Exception {
        ImmutableStringEnumSet z = ImmutableStringEnumSet.of(union);
        ImmutableStringEnumSet r = BASE.add(b);

        Assert.assertEquals(r, z);  // Set equals

        Assert.assertEquals(union, r.getBitmap()); // String equals
    }

    @Test
    public void testAdd() throws Exception {
        testAdd(ManyLetters.ALPHA,   "ABCDEF");
        testAdd(ManyLetters.BRAVO,   "BCDEF");
        testAdd(ManyLetters.CHARLIE, "BCDEF");
        testAdd(ManyLetters.FOXTROT, "BCDEF");
        testAdd(ManyLetters.XAVER,   "BCDEFX");
    }

    @Test
    public void testAddInBetween() throws Exception {
        ImmutableStringEnumSet a = ImmutableStringEnumSet.of("BD");
        ImmutableStringEnumSet b = a.add(ManyLetters.CHARLIE);
        ImmutableStringEnumSet c = ImmutableStringEnumSet.of("BCD");
        Assert.assertEquals(b, c);
    }

    private void testMinus(TokenizableEnum b, String difference) throws Exception {
        ImmutableStringEnumSet z = ImmutableStringEnumSet.of(difference);
        ImmutableStringEnumSet r = BASE.minus(b);

        Assert.assertEquals(r, z);  // Set equals

        Assert.assertEquals(difference, r.getBitmap()); // String equals
    }

    @Test
    public void testMinus() throws Exception {
        testMinus(ManyLetters.ALPHA,   "BCDEF");
        testMinus(ManyLetters.BRAVO,   "CDEF");
        testMinus(ManyLetters.CHARLIE, "BDEF");
        testMinus(ManyLetters.FOXTROT, "BCDE");
        testMinus(ManyLetters.XAVER,   "BCDEF");
    }
}
