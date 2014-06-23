package de.jpaw.bonaparte.benchmarks.map;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.logic.BlackHole;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

//Benchmark                                       Mode   Samples         Mean   Mean error    Units
//d.j.b.b.m.JavaAndGuava.guavaCacheGet           thrpt        40    92899.585     1513.430   ops/ms
//d.j.b.b.m.JavaAndGuava.javaConcurrentMapGet    thrpt        40   251024.900     2197.855   ops/ms
//d.j.b.b.m.JavaAndGuava.javaHashMapGet          thrpt        40   230614.641     4681.200   ops/ms

@State(value = Scope.Thread)
@OperationsPerInvocation(JavaAndGuava.OPERATIONS_PER_INVOCATION)
public class JavaAndGuava {
	static public final int OPERATIONS_PER_INVOCATION = 1000000;
	static public final String DATA = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet";
	static public final Long KEY = 437L;
	
	@GenerateMicroBenchmark
	public void javaHashMapGet(BlackHole bh) {
		final Map<Long,String> map = new HashMap<Long,String>(100);
		map.put(KEY, DATA);
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
	        bh.consume(map.get(KEY));
		}
	}

	@GenerateMicroBenchmark
	public void javaConcurrentMapGet(BlackHole bh) {
		final Map<Long,String> map = new ConcurrentHashMap<Long,String>(100);
		map.put(KEY, DATA);
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
	        bh.consume(map.get(KEY));
		}
	}

	@GenerateMicroBenchmark
	public void guavaCacheGet(BlackHole bh) {
		final Cache<Long,String> map = CacheBuilder
				.newBuilder()
				.expireAfterWrite(60, TimeUnit.SECONDS).build( 
 					new CacheLoader<Long, String>() {
 						public String load(Long key) {
 							return DATA;
 						}
 					});
		map.getIfPresent(KEY);  // get is put... (triggers loader)
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
	        bh.consume(map.getIfPresent(KEY));
		}
	}

}
