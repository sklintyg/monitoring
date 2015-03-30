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

import se.inera.monitoring.persistence.StatusRepository;
import se.inera.monitoring.persistence.UserCountRepository;
import se.inera.monitoring.web.domain.Status;
import se.inera.monitoring.web.domain.UserCount;

@Service
public class MonitoringServiceImpl implements MonitoringService {

    protected static final DateTimeFormatter formatter = new DateTimeFormatterFactory("yyyy-MM-dd HH:mm:ss").createDateTimeFormatter();

    @Autowired
    private UserCountRepository userCountRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public List<UserCount> getCountersBySystem(String system, int count) {
        ArrayList<UserCount> res = new ArrayList<>();
        for (se.inera.monitoring.persistence.dao.UserCount userCount : userCountRepository
                .findByServiceOrderByTimestampDesc(system, new PageRequest(0, count))) {
            res.add(new UserCount(userCount.getCount(), new LocalDateTime(
                    userCount.getTimestamp()).toString(formatter)));
        }
        Collections.reverse(res);
        return res;
    }

    @Override
    public List<Status> getStatusBySystem(String system) {
        List<Status> res = new ArrayList<>();
        for (se.inera.monitoring.persistence.dao.Status status : statusRepository.findByService(system)) {
            res.add(new Status(status.getSubservice(), status.getStatus(), status.getSeverity()));
        }
        return res;
    }
}
