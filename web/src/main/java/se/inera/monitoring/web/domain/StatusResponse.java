package se.inera.monitoring.web.domain;

import java.util.ArrayList;
import java.util.List;

public class StatusResponse {

    private String application;
    private String server;
    private long responsetime;
    private String timestamp;
    private String version;
    private boolean reachable = true;
    private List<Status> statuses = new ArrayList<>();

    public StatusResponse() {
    }

    public StatusResponse(String application, String server, long responsetime, String timestamp, int userCount, String version,
            List<Status> statuses) {
        this.application = application;
        this.server = server;
        this.responsetime = responsetime;
        this.timestamp = timestamp;
        this.version = version;
        this.statuses = statuses;
    }

    public static StatusResponse getUnreachable(String application, String server) {
        StatusResponse response = new StatusResponse();
        response.setApplication(application);
        response.setReachable(false);
        response.setServer(server);
        return response;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getResponsetime() {
        return responsetime;
    }

    public void setResponsetime(long responsetime) {
        this.responsetime = responsetime;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}
