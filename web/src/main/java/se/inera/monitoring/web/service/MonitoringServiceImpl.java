package se.inera.monitoring.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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

    @Autowired
    private WebcertServices webcertServices;

    @Autowired
    private MinaIntygServices minaIntygServices;

    @Autowired
    private StatistikServices statistikServices;

    @Override
    public List<UserCount> getCountersBySystem(String system, int count) {
        ArrayList<UserCount> res = new ArrayList<>();
        for (se.inera.monitoring.persistence.model.UserCount userCount : userCountRepository
                .findByServiceOrderByTimestampDesc(system, new PageRequest(0, count))) {
            res.add(convert(userCount));
        }
        Collections.reverse(res);
        return res;
    }

    @Override
    public List<Status> getStatusBySystem(String system) {
        List<Status> res = new ArrayList<>();
        for (String service : getFields(system)) {
            se.inera.monitoring.persistence.model.Status status = statusRepository.findByServiceAndSubserviceOrderByTimestampDesc(system, service);
            if (status != null) // If the status existed
                res.add(convert(status));
        }
        return res;
    }

    private HashSet<String> getFields(String system) {
        HashSet<String> fields = new HashSet<>();
        if ("webcert".equals(system)) {
            fields = webcertServices.getFields();
        } else if ("minaintyg".equals(system)) {
            // TODO
            fields = webcertServices.getFields();
        } else if ("minaintyg".equals(system)) {
            // TODO
            fields = webcertServices.getFields();
        }
        fields.add("version");
        return fields;
    }

    private static Status convert(se.inera.monitoring.persistence.model.Status modelStatus) {
        return new Status(modelStatus.getSubservice(), modelStatus.getStatus(), modelStatus.getSeverity());
    }

    private static UserCount convert(se.inera.monitoring.persistence.model.UserCount userCount) {
        return new UserCount(userCount.getCount(), new LocalDateTime(userCount.getTimestamp()).toString(formatter));
    }
}
