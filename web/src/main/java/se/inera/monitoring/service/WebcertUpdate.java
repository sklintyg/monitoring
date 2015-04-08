package se.inera.monitoring.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
import se.inera.monitoring.service.configuration.ServiceConfiguration;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

@Service
public class WebcertUpdate extends UpdateService {

    private static final Logger log = LoggerFactory
            .getLogger(WebcertUpdate.class);

    private static final String serviceName = "webcert";

    @Autowired
    private PingForConfigurationResponderInterface webcert;

    @Autowired
    private ServiceConfiguration config;

    @Autowired
    private ApplicationStatusRepository applicationStatus;

    @Scheduled(cron = "${webcert.ping.cron}")
    public void update() {
        log.debug("Updating status of webcert");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Ping webcert for its statuses
        PingForConfigurationResponseType response = webcert.pingForConfiguration("", new PingForConfigurationType());
        stopWatch.stop();

        // Convert the statuses to our internal Status model
        HashMap<String, SubsystemStatus> statuses = getStatuses(response.getConfiguration());

        // Save the applicationStatus
        applicationStatus.save(createApplicationStatus(7, stopWatch.getTotalTimeMillis(), "server1", new Date(), response.getVersion(),
                getSubsystems(statuses)));
    }

    private ApplicationStatus createApplicationStatus(Integer currentUsers, long responsetime, String server, Date timestamp, String version,
            List<SubsystemStatus> statuses) {
        ApplicationStatus status = new ApplicationStatus();
        status.setApplication(serviceName);
        status.setCurrentUsers(currentUsers);
        status.setId(UUID.randomUUID().toString());
        status.setResponsetime(responsetime);
        status.setServer(server);
        status.setTimestamp(timestamp);
        status.setVersion(version);
        status.setSubsystemStatus(statuses);
        return status;
    }

    private List<SubsystemStatus> getSubsystems(HashMap<String, SubsystemStatus> statuses) {
        List<SubsystemStatus> res = new ArrayList<>();
        for (String service : config.getService(serviceName).getConfigurations()) {
            if (statuses.containsKey(service)) {
                res.add(updateSeverity(statuses.get(service)));
            }
        }
        return res;
    }
}
