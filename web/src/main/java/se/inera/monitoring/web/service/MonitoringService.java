package se.inera.monitoring.web.service;

import java.util.List;

import se.inera.monitoring.web.domain.Status;
import se.inera.monitoring.web.domain.UserCount;

public interface MonitoringService {
    public List<UserCount> getCountersBySystem(String system, int count);

    public List<Status> getStatusBySystem(String system);
}
