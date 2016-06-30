package se.inera.monitoring.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

@Service
public class MonitoringServiceImpl implements MonitoringService {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoringService.class);


    @Autowired
    private ApplicationStatusRepository repo;

    @Override
    public List<UserCount> getCountersBySystem(String system) {
        LOG.info("Getting the latest counters for {}", system);
        List<UserCount> res = repo.getUserCount(system);
        return (res == null) ? new ArrayList<>() : res;
    }

    @Override
    public List<StatusResponse> getStatusBySystem(String system) {
        LOG.info("Getting all the latest statuses from {}", system);
        List<StatusResponse> res = repo.getStatus(system);
        return (res == null) ? new ArrayList<>() : res;
    }

}
