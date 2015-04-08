package se.inera.monitoring.persistence.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
import se.inera.monitoring.service.configuration.ServiceConfiguration;

@Service
public class UpdateStatuses {
    private Random rand = new Random();
    private String[] services = { "minaintyg", "statistik" };

    @Autowired
    private ApplicationStatusRepository repo;

    @Autowired
    private ServiceConfiguration config;

    @Scheduled(fixedDelay = 5000)
    public void update() {
        for (String service : services) {
            updateService(service);
        }
    }

    private void updateService(String service) {
        ApplicationStatus bogusStatus = new ApplicationStatus();
        bogusStatus.setApplication(service);
        bogusStatus.setCurrentUsers(rand.nextInt(1000));
        bogusStatus.setId(UUID.randomUUID().toString());
        bogusStatus.setResponsetime(0);
        bogusStatus.setServer("server1");
        bogusStatus.setTimestamp(new Date());
        bogusStatus.setVersion("fake");
        bogusStatus.setSubsystemStatus(generateStatuses(service));

        repo.save(bogusStatus);
    }

    private List<SubsystemStatus> generateStatuses(String service) {
        ArrayList<SubsystemStatus> res = new ArrayList<>();
        for (String subservice : config.getService(service).getConfigurations()) {
            SubsystemStatus newStatus = new SubsystemStatus();
            boolean fail = rand.nextBoolean();
            newStatus.setSeverity(fail ? 1 : 0);
            newStatus.setStatus(fail ? "fail" : "ok");
            newStatus.setSubsystem(subservice);
            res.add(newStatus);
        }
        return res;
    }
}
