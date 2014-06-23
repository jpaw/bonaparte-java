package de.jpaw.bonaparte.benchmarks.map;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.logic.BlackHole;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.config.MapConfig;

//Iteration   1: 75.198 ops/ms
//Iteration   2: 78.310 ops/ms
//Iteration   3: 79.675 ops/ms

@State(value = Scope.Thread)
@OperationsPerInvocation(HazelcastMap.OPERATIONS_PER_INVOCATION)
public class HazelcastMap {
	static public final int OPERATIONS_PER_INVOCATION = 1000000;
	static public final String DATA = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet";
	static public final Long KEY = 437L;

    private HazelcastInstance hz;
    private IMap<Long, String> map;

    @Setup
    public void setUp() {
        MapConfig mapConfig = new MapConfig("somemap");
        mapConfig.setAsyncBackupCount(0);
        mapConfig.setBackupCount(0);
        mapConfig.setStatisticsEnabled(false);

        Config config = new Config();
        //config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        //config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        //config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("192.168.1.107");
        //config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
        config.addMapConfig(mapConfig);
        hz = Hazelcast.newHazelcastInstance(config);
        map = hz.getMap(mapConfig.getName());
    }
    
    @TearDown
    public void tearDown() {
        Hazelcast.shutdownAll();
    }

	@GenerateMicroBenchmark
	public void hazelcastMapGet(BlackHole bh) {
		map.put(KEY, DATA);
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; ++i) {
	        bh.consume(map.get(KEY));
		}
	}
}
