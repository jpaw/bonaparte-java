package testcases.selfRefs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.ObjectReuseStrategy;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.pojos.selfRefs.AnotherElementWithRef;
import de.jpaw.bonaparte.pojos.selfRefs.ElementWithRef;

public class TestSelfRefs {

    @BeforeEach
    public void setDefaultStrategy() {
        ObjectReuseStrategy.defaultStrategy = ObjectReuseStrategy.BY_REFERENCE;
    }

    private ElementWithRef setup() {
        ElementWithRef r = new ElementWithRef();
        AnotherElementWithRef r2 = new AnotherElementWithRef();
        r.text = "root element";
        r.ref = r2;
        r2.text = "in another element";
        r2.ref1 = r;
        r2.ref2 = r2;
        return r;
    }

    private byte[] serializeByStrategyBAC(ElementWithRef b,
            ObjectReuseStrategy strategy) {
        ByteArrayComposer bac;
        if (strategy == null) {
            bac = new ByteArrayComposer();
        } else {
            bac = new ByteArrayComposer(strategy);
        }
        bac.writeRecord(b);
        return bac.getBytes();
    }

    private byte[] serializeByStrategySBC(ElementWithRef b,
            ObjectReuseStrategy strategy) {
        StringBuilderComposer sbc;
        StringBuilder buff = new StringBuilder(250);
        if (strategy == null) {
            sbc = new StringBuilderComposer(buff);
        } else {
            sbc = new StringBuilderComposer(buff, strategy);
        }
        sbc.writeRecord(b);
        return sbc.getBytes();
    }

    private ElementWithRef roundtrip(ElementWithRef b,
            ObjectReuseStrategy strategy, boolean useStringBuilderComposer,
            boolean useStringBuilderParser) throws MessageParserException {
        byte[] buffer;
        BonaPortable r;
        if (useStringBuilderComposer) {
            buffer = serializeByStrategySBC(b, strategy);
        } else {
            buffer = serializeByStrategyBAC(b, strategy);
        }
        if (useStringBuilderParser) {
            StringBuilder buff = new StringBuilder(new String(buffer));
            StringBuilderParser sbp = new StringBuilderParser(buff, 0, -1);
            r = sbp.readRecord();
        } else {
            r = new ByteArrayParser(buffer, 0, -1).readRecord();
        }
        return (ElementWithRef) r;
    }

    @Test
    public void testLengthsBAC() throws Exception {
        ElementWithRef b = setup();
        int l0 = serializeByStrategyBAC(b, null).length;
        System.out.println("Length BAC is " + l0);
    }

    @Test
    public void testLengthsSBC() throws Exception {
        ElementWithRef b = setup();
        int l0 = serializeByStrategySBC(b, null).length;
        System.out.println("Length SBC is " + l0);
    }

    @Test
    public void testObjectIdentitiesAfterDeserializationSBP()
            throws MessageParserException {
        ElementWithRef b = setup();
        for (int i = 0; i < 4; ++i) {
            boolean useSBC = (i & 1) != 0;
            boolean useSBP = (i & 2) != 0;
            System.out.println("Testing with "
                    + (useSBC ? "StringBuilder" : "ByteArray")
                    + "Composer and "
                    + (useSBP ? "StringBuilder" : "ByteArray") + "Parser");
            ElementWithRef b0 = roundtrip(b, null, useSBC, useSBP);

            assert (b0.ref.ref2 == b0.ref);
            assert (b0.ref.ref1 == b0);
        }
    }

    @Test
    public void justInfo() throws Exception {
        ElementWithRef b = setup();
        StringBuilder work = new  StringBuilder(200);
        StringBuilderComposer sbc = new StringBuilderComposer(work);
        sbc.writeRecord(b);
        System.out.println("Result is <" + work.toString() + ">");
    }


}
