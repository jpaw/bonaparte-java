package de.jpaw.bonaparte.benchmark.core;

import java.util.Date;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.coretests.initializers.FillBoxedTypes;
import de.jpaw.bonaparte.coretests.initializers.FillOtherTypes;
import de.jpaw.bonaparte.coretests.initializers.FillPrimitives;

public class Benchmark {
	public static void main(String[] args) throws Exception {
		int numberOfThreads = 1;
		int millionCallsPerThread = 1;
		int method = 0; 
		int objectId = 0;
		int initialBufferSize = 65500; 
		BonaPortable src = null;
		Thread threads [];
		Date start;
		Date stop;
		
		if (args.length > 0) {
			method = Integer.valueOf(args[0]);
		} else {
			System.out.println("Usage: Benchmark (method-id) [(threads) [(Mio calls / thread) [(object)]]]");
			System.exit(1);
		}
		if (args.length > 1) {
			numberOfThreads = Integer.valueOf(args[1]);
		}
		if (args.length > 2) {
			millionCallsPerThread = Integer.valueOf(args[2]);
		}
		if (args.length > 3) {
			objectId = Integer.valueOf(args[3]);
		}

		System.out.println("Starting benchmark method " + method + " with "
				+ numberOfThreads + " threads, "
				+ millionCallsPerThread + " million calls per thread"
				+ " with object " + objectId);
		
		start = new Date();
		threads = new Thread [numberOfThreads];
		switch (objectId) {
		case 0: 
			src = FillPrimitives.test1();
			break;
		case 1: 
			src = FillBoxedTypes.test1();
			break;
		case 2: 
			src = FillOtherTypes.test1();
			break;
		case 3: 
			src = FillOtherTypes.test2(1234567);
			break;
		}

		// start all the threads...
		for (int i = 0; i < numberOfThreads; ++i) {
			Runnable worker = new OneThread(src, method, millionCallsPerThread, initialBufferSize);
			threads[i] = new Thread(worker);
			threads[i].start();
		}
		// wait for the threads to finish...
		for (int i = 0; i < numberOfThreads; ++i) {
			threads[i].join();
		}
		stop = new Date();
		long millis = stop.getTime() - start.getTime();
		double callsPerMilliSecond = millionCallsPerThread * 1000000 * numberOfThreads / millis;
		System.out.println("Overall result for object " + src.get$PQON() + ": "
				+ (int)callsPerMilliSecond + " k calls / second");
	}

}
