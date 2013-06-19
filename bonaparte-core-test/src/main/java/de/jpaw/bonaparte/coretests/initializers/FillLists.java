package de.jpaw.bonaparte.coretests.initializers;

import java.util.ArrayList;
import java.util.List;

import de.jpaw.bonaparte.pojos.tests1.Lists;
import de.jpaw.bonaparte.pojos.tests1.Primitives;

public class FillLists {

    static public Lists test1() {
        Lists x = new Lists();

        List<Integer> iList = new ArrayList<Integer>(100);
        iList.add(23);
        iList.add(42);
        iList.add(Integer.valueOf(33));
        x.setIntList(iList);

        List<Primitives> primList = new ArrayList<Primitives>();
        primList.add(FillPrimitives.test1());
        primList.add(FillPrimitives.test1());
        x.setListOfObjects(primList);

        // no list at all
        x.setOptionalList(null);

        // list which exists but is empty
        x.setTextList(new ArrayList<String>(1000));
        return x;
    }
}
