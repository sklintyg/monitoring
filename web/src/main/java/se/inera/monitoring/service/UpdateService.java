package se.inera.monitoring.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
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
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.ConfigurationType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

@Service
public class UpdateService {
    private static final Logger log = LoggerFactory.getLogger(UpdateService.class);

    private static final int queueWarnThreshold = 5;
    private static final int queueFailThreshold = 10;

    public static final int OK = 0;
    public static final int FAIL = 1;
    public static final int WARN = 2;

    @Autowired
    private ServiceConfiguration config;

    @Autowired
    private ApplicationStatusRepository applicationStatus;

    @Scheduled(cron = "${ping.cron}")
    public void update() {
        for (se.inera.monitoring.service.configuration.Service service : config.getServices()) {
            log.debug(String.format("Updating status of %s", service.getServiceName()));

            for (Node node : config.getService(service.getServiceName()).getNodes()) {
                PingForConfigurationResponderInterface ping = getPingInterface(node.getNodeUrl());

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                PingForConfigurationResponseType response = ping.pingForConfiguration("", new PingForConfigurationType());
                stopWatch.stop();

                // Convert the statuses to our internal SubsystemStatus model
                HashMap<String, SubsystemStatus> statuses = getStatuses(response.getConfiguration());

                // Save the ApplicationStatus
                applicationStatus.save(createApplicationStatus(service.getServiceName(), new Random().nextInt(1000),
                        stopWatch.getTotalTimeMillis(), node.getNodeName(), new Date(), response.getVersion(),
                        getSubsystems(service.getServiceName(), statuses)));
            }
        }
    }

    private PingForConfigurationResponderInterface getPingInterface(String nodeUrl) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(PingForConfigurationResponderInterface.class);
        factory.setAddress(nodeUrl);
        PingForConfigurationResponderInterface ping = (PingForConfigurationResponderInterface) factory.create();
        return ping;
    }

    private ApplicationStatus createApplicationStatus(String serviceName, Integer currentUsers, long responsetime, String server, Date timestamp,
            String version,
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
    protected HashMap<String, SubsystemStatus> getStatuses(List<ConfigurationType> configurations) {
        HashMap<String, SubsystemStatus> res = new HashMap<>();
        for (ConfigurationType config : configurations) {
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
        if ("ok".equals(status.getStatus().trim().toLowerCase()))
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
            } else
                status.setSeverity(OK);
        }
        return status;
    }
}
