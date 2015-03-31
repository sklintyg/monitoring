package se.inera.monitoring.persistence.demo;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Random;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.StatusRepository;
import se.inera.monitoring.persistence.UserCountRepository;
import se.inera.monitoring.persistence.model.Status;
import se.inera.monitoring.persistence.model.UserCount;
import se.inera.monitoring.service.MinaIntygServices;
import se.inera.monitoring.service.StatistikServices;

@Service
public class RepositoryUpdate {

    @Autowired
    private StatusRepository statusRepo;

    @Autowired
    private UserCountRepository userRepo;

    @Autowired
    private MinaIntygServices minaIntygFields;

    @Autowired
    private StatistikServices statistikFields;

    @Scheduled(fixedDelay = 5000)
    public void updateDatabases() {
        updateStatus("minaintyg", "statistik");
        updateUserCount(RepositoryBootstrap.services);
    }

    private void updateUserCount(String... services) {
        for (String service : services) {
            Random rand = new Random();
            UserCount oldUsercount = userRepo
                    .findByServiceOrderByTimestampDesc(service,
                            new PageRequest(0, 1)).getContent().get(0);
            DateTime newTime = new DateTime(oldUsercount.getTimestamp())
                    .plusMinutes(1);
            int newCount = oldUsercount.getCount()
                    + rand.nextInt(RepositoryBootstrap.maxDiff)
                    - RepositoryBootstrap.maxDiff / 2;
            newCount = Math.max(Math.min(newCount, RepositoryBootstrap.max),
                    RepositoryBootstrap.min);
            UserCount newUserCount = new UserCount();
            newUserCount.setCount(newCount);
            newUserCount.setService(service);
            newUserCount.setTimestamp(new Timestamp(newTime.getMillis()));
            userRepo.save(newUserCount);
        }
    }

    private void updateStatus(String... services) {
        Random rand = new Random();
        Timestamp now = new Timestamp(DateTime.now().getMillis());
        for (String service : services) {
            for (String field : getFields(service)) {
                Status status = new Status();
                boolean isOk = rand.nextBoolean();
                status.setStatus(isOk ? "OK" : "FAIL");
                status.setSeverity(isOk ? 0 : 1);
                status.setTimestamp(now);
                status.setService(service);
                status.setSubservice(field);
                statusRepo.save(status);
            }
            Status version = new Status(service, "version", "fake;" + now.toString(), 0, now);
            statusRepo.save(version);
        }
    }

    private HashSet<String> getFields(String system) {
        HashSet<String> fields = new HashSet<>();
        if ("webcert".equals(system)) {
            // Not needed as real webcert is present
        } else if ("minaintyg".equals(system)) {
            fields.addAll(minaIntygFields.getFields());
        } else if ("statistik".equals(system)) {
            fields.addAll(statistikFields.getFields());
        }
        return fields;
    }

}
