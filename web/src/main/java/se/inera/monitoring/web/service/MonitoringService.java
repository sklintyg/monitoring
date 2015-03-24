package se.inera.monitoring.web.service;

import se.inera.monitoring.web.domain.Counters;

public interface MonitoringService {
	public Counters getCountersBySystem(String system);
}
