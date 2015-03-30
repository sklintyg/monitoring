package se.inera.monitoring.web.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import se.inera.monitoring.persistence.model.Status;
import se.riv.itintegration.monitoring.v1.ConfigurationType;

public abstract class UpdateService {

    private static final int queueWarnThreshold = 5;
    private static final int queueFailThreshold = 10;

    public static final int OK = 0;
    public static final int FAIL = 1;
    public static final int WARN = 2;

    /**
     * Returns the configurations acquired from the server but converted to statuses we can save in our database
     * 
     * @param configurations
     *            The configurations
     * @param service
     *            The current service (e.g. "webcert" or "minaintyg")
     * @param now
     *            The time (we want to use the same time for each status acquired by the same PingFor.. call
     * @return An HashMap with the subservice name as key and the new status as value
     */
    protected HashMap<String, Status> getStatuses(List<ConfigurationType> configurations, String service, DateTime now) {
        HashMap<String, Status> res = new HashMap<>();
        for (ConfigurationType config : configurations) {
            res.put(config.getName(), new Status(service, config.getName(), config.getValue(), 0, new Timestamp(now.getMillis())));
        }
        return res;
    }

    /**
     * Updates the severity of a stautus. We do not want to do this for statuses we are not interested in.
     * 
     * If the status is some variant of "ok" we deem the status to be OK, if it is some variant of "fail" we deem it to
     * be of status FAIL. If the status contain the word queue we classify the status as a measure of an queue size.
     * Here we have a threshold for FAIL and WARN.
     * 
     * @param status
     *            The status to be changed
     * @return The same status but with updated severity
     */
    protected Status updateSeverity(Status status) {
        if ("ok".equals(status.getStatus().trim().toLowerCase()))
            status.setSeverity(OK);
        else if ("fail".equals(status.getStatus().trim().toLowerCase()))
            status.setSeverity(FAIL);
        else {
            // TODO Now it gets trickier. We want to warn when queuesize is too high. When is the queue size too high?
            if (status.getSubservice().toLowerCase().contains("queue")) {
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
