package de.jpaw.bonaparte.coretests.initializers;

import java.util.HashMap;

import de.jpaw.bonaparte.pojos.mapTests.AlphaColor;
import de.jpaw.bonaparte.pojos.mapTests.Maps1;
import de.jpaw.bonaparte.pojos.mapTests.Unrelated;

public class FillMaps {

    static public Maps1 test1() {
        Maps1 x = new Maps1();
        
        x.primes = new HashMap<Integer, Integer>(8);
        x.primes.put(1, 2);
        x.primes.put(2, 3);
        x.primes.put(3, 5);
        x.primes.put(4, 7);
        x.primes.put(5, 11);
        
        x.ccConverter = new HashMap<String, String>(9);
        x.ccConverter.put("DE", "Germany");
        x.ccConverter.put("IE", "Ireland");
        x.ccConverter.put("US", "United States");
        x.ccConverter.put("NA", null);
        
        x.primaryColor = new HashMap<String, AlphaColor>(11);
        x.primaryColor.put("orange", AlphaColor.RED);
        x.primaryColor.put("deepgreen", AlphaColor.GREEN);
        
        x.longToBonaPortable = new HashMap<Long, Unrelated>(12);
        x.longToBonaPortable.put(Long.valueOf(42L), new Unrelated(4242, "Fortytwotimestwo"));
        x.longToBonaPortable.put(Long.valueOf(101L), new Unrelated(-1, "oneoone"));
        return x;
    }

}
