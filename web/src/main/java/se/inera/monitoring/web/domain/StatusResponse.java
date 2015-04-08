package se.inera.monitoring.web.domain;

import java.util.List;

public class StatusResponse {

    private String application;
    private String server;
    private long responsetime;
    private String timestamp;
    private int userCount;
    private String version;
    private List<Status> statuses;

    public StatusResponse() {
    }

    public StatusResponse(String application, String server, long responsetime, String timestamp, int userCount, String version,
            List<Status> statuses) {
        this.application = application;
        this.server = server;
        this.responsetime = responsetime;
        this.timestamp = timestamp;
        this.userCount = userCount;
        this.version = version;
        this.statuses = statuses;
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

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
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
}
