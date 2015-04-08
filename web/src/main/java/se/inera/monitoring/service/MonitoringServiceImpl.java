package se.inera.monitoring.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
import se.inera.monitoring.web.domain.Status;
import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

@Service
public class MonitoringServiceImpl implements MonitoringService {

    protected static final DateTimeFormatter formatter = new DateTimeFormatterFactory("yyyy-MM-dd HH:mm:ss").createDateTimeFormatter();

    @Autowired
    private ApplicationStatusRepository repo;

    @Override
    public List<UserCount> getCountersBySystem(String system, int count) {
        ArrayList<UserCount> res = new ArrayList<>();
        for (ApplicationStatus status : repo.findByApplicationOrderByTimestampDesc(system, new PageRequest(0, count))) {
            res.add(convert(status));
        }
        Collections.reverse(res);
        return res;
    }

    @Override
    public StatusResponse getStatusBySystem(String system) {
        ApplicationStatus status = repo.findByApplicationOrderByTimestampDesc(system);
        return new StatusResponse(system, status.getServer(), status.getResponsetime(),
                new LocalDateTime(status.getTimestamp()).toString(formatter),
                (int) status.getCurrentUsers(), status.getVersion(),
                convert(status.getSubsystemStatus()));
    }

    private List<Status> convert(List<SubsystemStatus> subsystemStatus) {
        ArrayList<Status> res = new ArrayList<>();
        for (SubsystemStatus status : subsystemStatus) {
            res.add(new Status(status.getSubsystem(), status.getStatus(), status.getSeverity()));
        }
        return res;
    }

    private static UserCount convert(ApplicationStatus status) {
        return new UserCount(status.getCurrentUsers(), new LocalDateTime(status.getTimestamp()).toString(formatter));
    }
}