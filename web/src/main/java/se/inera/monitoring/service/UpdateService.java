package se.inera.monitoring.service;

import java.sql.Timestamp;
import java.util.*;

import javax.xml.ws.WebServiceException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
import se.inera.monitoring.service.configuration.Node;
import se.inera.monitoring.service.configuration.ServiceConfiguration;

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
    private static final String CURRENT_USERS_NAME = "nbrOfUsers";

    private static final Logger log = LoggerFactory.getLogger(UpdateService.class);

    protected static final int queueWarnThreshold = 5;
    protected static final int queueFailThreshold = 10;

    public static final int OK = 0;
    public static final int FAIL = 1;
    public static final int WARN = 2;

    @Autowired
    private PingForConfigurationFactory pingFactory;

    @Autowired
    private ServiceConfiguration config;

    @Autowired
    private ApplicationStatusRepository applicationStatus;

    /**
     * Updates the services defined in the configuration file
     *
     * The cron expression should not be more than once every minute
     */
    @Scheduled(cron = "${ping.cron}")
    public void update() {
        Timestamp timestamp = new Timestamp(new DateTime().getMillis());
        for (se.inera.monitoring.service.configuration.Service service : config.getServices()) {
            log.debug(String.format("Updating status of %s", service.getServiceName()));

            for (Node node : config.getService(service.getServiceName()).getNodes()) {

                StopWatch stopWatch = new StopWatch();
                ConfigResponse response = null;
                try {
                    stopWatch.start();
                    response = pingFactory.ping(node.getNodeUrl(), service.getVersion());
                    stopWatch.stop();

                    // Convert the statuses to our internal SubsystemStatus model
                    HashMap<String, SubsystemStatus> statuses = getStatuses(response.getConfiguration());

                    int currentUsers = 0;
                    try {
                        currentUsers = getCurrentUsers(response.getConfiguration());
                    } catch (NumberFormatException e) {
                        log.warn(String.format("Could not parse the number of users from %s at node %s", service.getServiceName(), node.getNodeName()));
                    }

                    applicationStatus.save(ApplicationStatus.getApplicationStatus(service.getServiceName(), currentUsers,
                            stopWatch.getTotalTimeMillis(), node.getNodeName(), timestamp, response.getVersion(),
                            getSubsystems(service.getServiceName(), statuses)));
                } catch (IllegalArgumentException | WebServiceException | ServiceNotReachableException e1) {
                    log.error(String.format("Could not reach %s at %s located at URL %s", service.getServiceName(), node.getNodeName(),
                            node.getNodeUrl()));
                    applicationStatus.save(ApplicationStatus.getUnreachable(service.getServiceName(), node.getNodeName(), timestamp));
                }
            }
        }
    }

    /**
     * The number of current users are presented as one of the subsystem status. This is not ideal and we want to
     * present it as a mandatory field for the status of the node.
     *
     * This is why we loop through the available configurations (which are around 5-8) until we find the configuration
     * with the name of {@linkplain UpdateService#CURRENT_USERS_NAME}
     *
     * @param configurations
     * @return
     */
    private int getCurrentUsers(List<Configuration> configurations) {
        for (Configuration config : configurations) {
            if (CURRENT_USERS_NAME.equals(config.getName())) {
                return Integer.parseInt(config.getValue());
            }
        }
        return 0;
    }

    /**
     * Retrieves the relevant subsystems defined in the configuration in {@linkplain ServiceConfiguration}
     *
     * @param serviceName
     * @param statuses
     * @return
     */
    private List<SubsystemStatus> getSubsystems(String serviceName, HashMap<String, SubsystemStatus> statuses) {
        List<SubsystemStatus> res = new ArrayList<>();
        for (String service : config.getService(serviceName).getConfigurations()) {
            if (statuses.containsKey(service)) {
                res.add(updateSeverity(statuses.get(service)));
            }
        }
        return res;
    }

    /**
     * Returns the configurations acquired from the server but converted to statuses we can save in our database
     *
     * @param configurations
     *            The configurations
     * @param now
     *            The time (we want to use the same time for each status acquired by the same PingFor.. call
     * @return An HashMap with the subservice name as key and the new status as value
     */
    protected HashMap<String, SubsystemStatus> getStatuses(List<Configuration> configurations) {
        HashMap<String, SubsystemStatus> res = new HashMap<>();
        for (Configuration config : configurations) {
            SubsystemStatus status = new SubsystemStatus();
            status.setSubsystem(config.getName());
            status.setStatus(config.getValue());
            status.setSeverity(0);
            res.put(config.getName(), status);
        }
        return res;
    }

    /**
     * Updates the severity of a status. We do not want to do this for statuses we are not interested in.
     *
     * If the status is some variant of "ok" we deem the status to be OK, if it is some variant of "fail" we deem it to
     * be of status FAIL. If the status contain the word queue we classify the status as a measure of an queue size.
     * Here we have a threshold for FAIL and WARN.
     *
     * @param status
     *            The status to be changed
     * @return The same status but with updated severity
     */
    protected SubsystemStatus updateSeverity(SubsystemStatus status) {
        if (status.getStatus() == null || status.getSubsystem() == null)
            return status;
        else if ("ok".equals(status.getStatus().trim().toLowerCase()))
            status.setSeverity(OK);
        else if ("fail".equals(status.getStatus().trim().toLowerCase()))
            status.setSeverity(FAIL);
        else {
            // TODO Now it gets trickier. We want to warn when queuesize is too high. When is the queue size too high?
            if (status.getSubsystem().toLowerCase().contains("queue")) {
                int queueSize = Integer.parseInt(status.getStatus());
                if (queueSize >= queueFailThreshold)
                    status.setSeverity(FAIL);
                else if (queueSize >= queueWarnThreshold)
                    status.setSeverity(WARN);
                else
                    status.setSeverity(OK);
            }
        }
        return status;
    }
}
