package se.inera.monitoring.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.StatusRepository;
import se.inera.monitoring.persistence.dao.Status;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.ConfigurationType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

@Service
public class WebcertUpdate {

    private static final Logger log = LoggerFactory
            .getLogger(WebcertUpdate.class);

    private static final String serviceName = "webcert";

    @Autowired
    private PingForConfigurationResponderInterface webcert;

    @Autowired
    private StatusRepository statusRepo;

    @Scheduled(cron = "${webcert.ping.cron}")
    public void update() {
        log.debug("Updating webcert status");
        PingForConfigurationResponseType response = webcert.pingForConfiguration("", new PingForConfigurationType());
        List<Status> existing = statusRepo.findByService(serviceName);
        for (ConfigurationType status : response.getConfiguration()) {
            Status newStatus = new Status();
            newStatus.setService(serviceName);
            newStatus.setSubservice(status.getName());

            int index = existing.indexOf(newStatus);
            if (index != -1) {
                newStatus = existing.get(index);
            }

            newStatus.setSeverity(0);
            newStatus.setStatus(status.getValue());
            statusRepo.save(newStatus);
        }
    }
}
