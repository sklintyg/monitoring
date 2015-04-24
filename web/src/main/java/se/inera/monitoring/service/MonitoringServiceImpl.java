package se.inera.monitoring.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
import se.inera.monitoring.service.configuration.Node;
import se.inera.monitoring.service.configuration.ServiceConfiguration;
import se.inera.monitoring.web.domain.Status;
import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

@Service
public class MonitoringServiceImpl implements MonitoringService {

    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);

    protected static final DateTimeFormatter formatter = new DateTimeFormatterFactory("yyyy-MM-dd HH:mm").createDateTimeFormatter();

    @Autowired
    private ApplicationStatusRepository repo;

    @Autowired
    private ServiceConfiguration config;

    @Override
    public List<UserCount> getCountersBySystem(String system, int minutes) {
        log.info("Getting the {} minutes latest counters for {}", minutes, system);
        se.inera.monitoring.service.configuration.Service service = config.getService(system);
        ArrayList<UserCount> res = new ArrayList<>();
        if (service == null)
            return res;
        int count = service.getNodes().size() * minutes;
        for (ApplicationStatus status : repo.findByApplicationOrderByTimestampDescServerDesc(system, new PageRequest(0, count))) {
            res.add(getUserCount(status));
        }
        Collections.reverse(res);
        return res;
    }

    @Override
    public List<StatusResponse> getStatusBySystem(String system) {
        log.info("Getting all the latest statuses from {}", system);
        List<StatusResponse> res = new ArrayList<>();
        se.inera.monitoring.service.configuration.Service service = config.getService(system);
        if (service == null)
            return res;

        for (Node node : service.getNodes()) {
            List<ApplicationStatus> resp = repo.findByApplicationAndServerOrderByTimestampDesc(system, node.getNodeName(), new PageRequest(0, 1));
            if (resp != null && resp.size() != 0) { // Get the latest entry
                ApplicationStatus status = resp.get(0);
                res.add(getStatusResponse(status));
            } else {
                log.warn("Could not find status for {} at {}", system, node.getNodeName());
            }
        }
        return res;
    }

    private static StatusResponse getStatusResponse(ApplicationStatus appStatus) {
        StatusResponse res = new StatusResponse();
        res.setApplication(appStatus.getApplication());
        res.setReachable(appStatus.isReachable());
        res.setResponsetime(appStatus.getResponsetime());
        res.setServer(appStatus.getServer());
        res.setStatuses(convert(appStatus.getSubsystemStatuses()));
        res.setTimestamp(new LocalDateTime(appStatus.getTimestamp()).toString(formatter));
        res.setUserCount(appStatus.getCurrentUsers());
        res.setVersion(appStatus.getVersion());
        return res;
    }

    private static List<Status> convert(List<SubsystemStatus> subsystemStatus) {
        ArrayList<Status> res = new ArrayList<>();
        for (SubsystemStatus status : subsystemStatus) {
            res.add(new Status(status.getSubsystem(), status.getStatus(), status.getSeverity()));
        }
        return res;
    }

    private static UserCount getUserCount(ApplicationStatus status) {
        return new UserCount(status.getServer(), status.getCurrentUsers(), new LocalDateTime(status.getTimestamp()).toString(formatter));
    }
}
