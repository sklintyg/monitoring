package se.inera.monitoring.persistence.demo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
import se.inera.monitoring.service.configuration.ServiceConfiguration;

@org.springframework.stereotype.Service
public class UpdateStatuses {
    private Random rand = new Random();
    private String[] services = { "minaintyg", "statistik" };
    private String[] subservices = { "hsa", "intygstjansten" };

    @Autowired
    private ApplicationStatusRepository repo;

    @Autowired
    private ServiceConfiguration config;

//    @Scheduled(cron = "${ping.cron}")
    public void update() {
        for (String service : services) {
            updateService(service);
        }
    }

    private void updateService(String service) {
        List<ApplicationStatus> lastStatus = repo.findByApplicationOrderByTimestampDescServerDesc(service, new PageRequest(0, 1));
        int lastCurrentUsers = 0;
        if (lastStatus != null && !lastStatus.isEmpty()) {
            lastCurrentUsers = lastStatus.get(0).getCurrentUsers();
        }

        ApplicationStatus bogusStatus = new ApplicationStatus();

        bogusStatus.setApplication(service);
        bogusStatus.setCurrentUsers(Math.min(Math.max(lastCurrentUsers + (rand.nextInt(20) - 10), 0), 1000));
        bogusStatus.setResponsetime(0);
        bogusStatus.setServer("server1");
        bogusStatus.setTimestamp(new Timestamp(new DateTime().getMillis()));
        bogusStatus.setVersion("fake");
        bogusStatus.setSubsystemStatuses(generateStatuses(service));

        repo.save(bogusStatus);
    }

    private List<SubsystemStatus> generateStatuses(String serviceName) {
        ArrayList<SubsystemStatus> res = new ArrayList<>();

        for (String subservice : subservices) {
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
