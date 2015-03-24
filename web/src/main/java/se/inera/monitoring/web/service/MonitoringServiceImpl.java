package se.inera.monitoring.web.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import se.inera.monitoring.web.domain.Counters;

@Service
public class MonitoringServiceImpl implements MonitoringService {

    // TODO - REMOVE
    // This is just for fake!!!
    private static final Map<String, Integer> fakeUserCountMap;
    static {
        Map<String, Integer> aMap = new HashMap<String, Integer>();
        aMap.put("webcert", 111);
        aMap.put("minaintyg", 222);
        aMap.put("statistik", 333);
        fakeUserCountMap = Collections.unmodifiableMap(aMap);
    }    
    
	@Override
	public Counters getCountersBySystem(String system) {
		Counters counters = new Counters();
		Integer userCount = fakeUserCountMap.get(system);
		if (userCount == null) {
		    userCount = -1;
		}
		counters.setUserCount(userCount);
		return counters;
	}
}
