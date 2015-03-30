package se.inera.monitoring.service;

import java.sql.Timestamp;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.StatusRepository;
import se.inera.monitoring.persistence.model.Status;
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
    private StatusRepository statusRepo;

    @Autowired
    private WebcertServices webcertServices;

    @Scheduled(cron = "${webcert.ping.cron}")
    public void update() {
        log.debug("Updating status of webcert");
        DateTime now = DateTime.now();

        // Ping webcert for its statuses
        PingForConfigurationResponseType response = webcert.pingForConfiguration("", new PingForConfigurationType());

        // Convert the statuses to our internal Status model
        HashMap<String, Status> statuses = getStatuses(response.getConfiguration(), serviceName, now);
        // Save the statuses we are interested in
        for (String service : webcertServices.getFields()) {
            if (statuses.containsKey(service))
                statusRepo.save(updateSeverity(statuses.get(service)));
        }
        // Save the version number
        statusRepo.save(new Status(serviceName, "version", response.getVersion() + ";" + now.toString(MonitoringServiceImpl.formatter), 0,
                new Timestamp(now.getMillis())));
    }
}
