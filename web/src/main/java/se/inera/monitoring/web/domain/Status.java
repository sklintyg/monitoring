package se.inera.monitoring.web.domain;

import se.inera.monitoring.service.configuration.ConfigType;

public class Status {

    private final String servicename;
    private final String statuscode;
    private final int severity;
    private final ConfigType type;

    public Status(String servicename, String statusCode, int severity, ConfigType type) {
        this.servicename = servicename;
        this.statuscode = statusCode;
        this.severity = severity;
        this.type = type;
    }

    public String getServiceName() {
        return servicename;
    }

    public String getStatuscode() {
        return statuscode;
    }

    public int getSeverity() {
        return severity;
    }

    public ConfigType getType() {
        return type;
    }
}
