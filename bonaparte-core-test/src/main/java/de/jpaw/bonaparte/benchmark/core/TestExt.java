package de.jpaw.bonaparte.benchmark.core;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import de.jpaw.bonaparte.benchmark.serializables.FillBoxedTypesS;
import de.jpaw.bonaparte.benchmark.serializables.FillPrimitivesS;
import de.jpaw.bonaparte.coretests.initializers.FillBoxedTypes;
import de.jpaw.bonaparte.coretests.initializers.FillPrimitives;
import de.jpaw.bonaparte.pojos.tests1.BoxedTypes;
import de.jpaw.bonaparte.pojos.tests1.BoxedTypesS;
import de.jpaw.bonaparte.pojos.tests1.Primitives;
import de.jpaw.bonaparte.pojos.tests1.PrimitivesS;


public class TestExt {
	private static void serializableAlternative(int callsPerThread, Serializable x) {
		try {
			for (int i = 0; i < callsPerThread; ++i) {
				ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);
				ObjectOutputStream o = new ObjectOutputStream(fos);
				o.writeObject(x);
				o.close();
				byte[] result = fos.toByteArray();
				if (i == 0)
					System.out.println("Length of buffer is " + result.length);
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	static private void runBench(int millionCallsPerThread, Serializable x) {
		Date start = new Date();
		serializableAlternative(millionCallsPerThread * 1000000, x);
		Date stop = new Date();
		long millis = stop.getTime() - start.getTime();
		double callsPerMilliSecond = millionCallsPerThread * 1000000 / millis;
		System.out.println("Overall result for object " + x.getClass().getSimpleName() + ": "
				+ (int)callsPerMilliSecond + " k calls / second");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int millionCallsPerThread = 1;
		Primitives p = FillPrimitives.test1();
		BoxedTypes b = FillBoxedTypes.test1();
		PrimitivesS ps = FillPrimitivesS.test1();
		BoxedTypesS bs = FillBoxedTypesS.test1();
		
		// run multiple tests to avoid effects of JIT kicking in
		runBench(millionCallsPerThread, b);
		runBench(millionCallsPerThread, p);
		runBench(millionCallsPerThread, bs);
		runBench(millionCallsPerThread, ps);
		runBench(millionCallsPerThread, b);
		runBench(millionCallsPerThread, p);
		runBench(millionCallsPerThread, bs);
		runBench(millionCallsPerThread, ps);
	}
	/*
	 * Results on my machine:
Length of buffer is 102
Overall result for object BoxedTypes: 775 k calls / second
Length of buffer is 102
Overall result for object Primitives: 975 k calls / second
Length of buffer is 762
Overall result for object BoxedTypesS: 154 k calls / second
Length of buffer is 195
Overall result for object PrimitivesS: 648 k calls / second
Length of buffer is 102
Overall result for object BoxedTypes: 1196 k calls / second
Length of buffer is 102
Overall result for object Primitives: 1157 k calls / second
Length of buffer is 762
Overall result for object BoxedTypesS: 157 k calls / second
Length of buffer is 195
Overall result for object PrimitivesS: 646 k calls / second
	=> Boxed types are a factor 8 faster with Bonaparte, and use only 14% of the space
	=> for primitives, the difference is "only" a factor of 2
	 */

}
