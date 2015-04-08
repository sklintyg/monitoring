package se.inera.monitoring.service;

import java.util.List;

import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

public interface MonitoringService {
    public List<UserCount> getCountersBySystem(String system, int count);

    public StatusResponse getStatusBySystem(String system);
}
