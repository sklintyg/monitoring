package se.inera.monitoring.service;

import java.util.*;

import javax.xml.ws.WebServiceException;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.service.configuration.Node;
import se.inera.monitoring.service.configuration.ServiceConfiguration;
import se.inera.monitoring.web.domain.*;

/**
 * Service which handles updating the monitored services. The configuration of nodes and url's are found by the
 * {@link ServiceConfiguration}
 *
 * @author kaan
 *
 */
@Service
public class UpdateService {

    /**
     * The String which identifies the number of currently logged in users in the service
     */
    protected static final String CURRENT_USERS_NAME = "nbrOfUsers";

    private static final Logger log = LoggerFactory.getLogger(UpdateService.class);

    protected static final int queueWarnThreshold = 5;
    protected static final int queueFailThreshold = 10;

    public static final int OK = 0;
    public static final int FAIL = 1;
    public static final int WARN = 2;

    protected static final DateTimeFormatter formatter = new DateTimeFormatterFactory("yyyy-MM-dd HH:mm").createDateTimeFormatter();

    @Autowired
    private PingForConfigurationFactory pingFactory;

    @Autowired
    private ServiceConfiguration config;

    @Autowired
    private ApplicationStatusRepository repo;

    /**
     * Updates the services defined in the configuration file
     */
    @Scheduled(cron = "${ping.cron}")
    public void update() {
        LocalDateTime timestamp = LocalDateTime.now();
        for (se.inera.monitoring.service.configuration.Service service : config.getServices()) {
            log.debug(String.format("Updating status of %s", service.getServiceName()));

            for (Node node : service.getNodes()) {

                StopWatch stopWatch = new StopWatch();
                ConfigResponse response = null;
                try {
                    stopWatch.start();
                    response = pingFactory.ping(node.getNodeUrl(), service.getVersion());
                    stopWatch.stop();

                    StatusResponse sr = new StatusResponse();
                    sr.setApplication(service.getServiceName());
                    sr.setResponsetime(stopWatch.getTotalTimeMillis());
                    sr.setReachable(true);
                    sr.setServer(node.getNodeName());
                    sr.setStatuses(getStatuses(service, response.getConfiguration()));
                    sr.setTimestamp(timestamp.toString(formatter));
                    sr.setVersion(response.getVersion());

                    repo.save(sr);
                    repo.save(new UserCount(node.getNodeName(), getCurrentUsers(response.getConfiguration()), timestamp.toString(formatter)),
                            service.getServiceName());

                } catch (IllegalArgumentException | WebServiceException | ServiceNotReachableException e1) {
                    log.error(String.format("Could not reach %s at %s located at URL %s", service.getServiceName(), node.getNodeName(),
                            node.getNodeUrl()));
                    repo.save(StatusResponse.getUnreachable(service.getServiceName(), node.getNodeName()));
                    repo.save(UserCount.getUnreachable(node.getNodeName(), timestamp.toString(formatter)), service.getServiceName());
                }
            }
        }

    }

    private int getCurrentUsers(List<Configuration> configurations) {
        Optional<Configuration> configuration = configurations.stream().filter(c -> CURRENT_USERS_NAME.equals(c.getName())).findFirst();
        if (configuration.isPresent()) {
            try {
                return Integer.parseInt(configuration.get().getValue());
            } catch (NumberFormatException e) {
                log.warn("Could not parse the number of users from {}", configuration.get().getValue());
            }
        }
        return 0;
    }

    protected List<Status> getStatuses(se.inera.monitoring.service.configuration.Service service, List<Configuration> configurations) {
        ArrayList<Status> res = new ArrayList<>();
        for (Configuration configuration : configurations) {
            if (service.getConfigurations().contains(configuration.getName())) {
                res.add(new Status(configuration.getName(), configuration.getValue(),
                        getSeverity(configuration.getName(), configuration.getValue())));
            }
        }
        return res;
    }

    protected int getSeverity(String name, String status) {
        switch (status.trim().toLowerCase()) {
        case "ok":
            return OK;
        case "fail":
            return FAIL;
        default:
            // Now it gets trickier. We want to warn when queuesize is too high. When is the queue size too high?
            if (name.toLowerCase().contains("queue")) {
                int queueSize = Integer.parseInt(status);
                if (queueSize >= queueFailThreshold)
                    return FAIL;
                else if (queueSize >= queueWarnThreshold)
                    return WARN;
                else
                    return OK;
            } else { // We cant parse it
                return OK;
            }
        }
    }
}
