package se.inera.monitoring.service;

import java.util.List;

import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

/**
 * Service handles the retrieval of monitoring data.
 *
 * @author kaan
 *
 */
public interface MonitoringService {
    public List<UserCount> getCountersBySystem(String system);

    public List<StatusResponse> getStatusBySystem(String system);
}
