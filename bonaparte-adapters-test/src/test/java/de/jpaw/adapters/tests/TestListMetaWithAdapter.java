package de.jpaw.adapters.tests;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.DataAndMeta;
import de.jpaw.bonaparte.core.ListMetaComposer;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Millis;
import de.jpaw.bonaparte.pojos.adapters.fixedpointToBigDecimal.BigMillis;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomMillis;
import de.jpaw.fixedpoint.types.MilliUnits;

public class TestListMetaWithAdapter {

    @Test
    public void testListMetaComposer() throws Exception {
        CustomMillis m1 = new CustomMillis("hello", MilliUnits.of(2718), MilliUnits.of(3142));
        ListMetaComposer c1 = new ListMetaComposer(false, false, false);
        c1.writeRecord(m1);
        List<DataAndMeta> dwm = c1.getStorage();

        Assertions.assertEquals(dwm.size(), 3);
        Assertions.assertEquals(dwm.get(1).data, Long.valueOf(2718));
        Assertions.assertEquals(dwm.get(1).meta, Millis.meta$$mantissa);
        Assertions.assertEquals(dwm.get(2).data, BigDecimal.valueOf(3142, 3));
        Assertions.assertEquals(dwm.get(2).meta, BigMillis.meta$$mantissa);
    }

    // see a different result with the last parameter of ListMetaComposer set to true
    @Test
    public void testListMetaComposerKeepExternals() throws Exception {
        CustomMillis m1 = new CustomMillis("hello", MilliUnits.of(2718), MilliUnits.of(3142));
        ListMetaComposer c1 = new ListMetaComposer(false, false, true);
        c1.writeRecord(m1);
        List<DataAndMeta> dwm = c1.getStorage();

        Assertions.assertEquals(dwm.size(), 3);
        Assertions.assertEquals(dwm.get(1).data, MilliUnits.of(2718));
        Assertions.assertEquals(dwm.get(1).meta, CustomMillis.meta$$myIntegralMillis);
        Assertions.assertEquals(dwm.get(2).data, MilliUnits.of(3142));
        Assertions.assertEquals(dwm.get(2).meta, CustomMillis.meta$$myBigDecimalMillis);
    }
}
