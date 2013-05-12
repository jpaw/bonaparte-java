package de.jpaw.bonaparte.benchmark.core;

import de.jpaw.bonaparte.coretests.initializers.FillMaps;
import de.jpaw.bonaparte.coretests.initializers.FillOtherTypes;
import de.jpaw.bonaparte.pojos.mapTests.Maps1;
import de.jpaw.bonaparte.pojos.mapTests.Sets1;
import de.jpaw.bonaparte.pojos.tests1.OtherTypes;
import de.jpaw.util.ToStringHelper;

public class ToString {

    /**
     * @param args
     */
    public static void main(String[] args) {
        OtherTypes test = FillOtherTypes.test1();
        System.out.println("Test data short is " + test.toString() + "!");
        System.out.println("Test data long is " + ToStringHelper.toStringML(test) + "!");

        test.setTimestamp2(null);
        System.out.println("Test data short is " + test.toString() + "!");
        System.out.println("Test data long is " + ToStringHelper.toStringML(test) + "!");

        Maps1 maptest = FillMaps.test1();
        maptest.longToBonaPortable.put(Long.valueOf(0L), null);     // violation of required spec in bon, but want to show null output here
        System.out.println("Map output is " + ToStringHelper.toStringML(maptest) + "!");
        
        Sets1 setTest = FillMaps.testSets1();
        System.out.println("Set output is " + ToStringHelper.toStringML(setTest) + "!");
        
    }

}
