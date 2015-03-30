package se.inera.monitoring.web.domain;

import java.util.List;

public class StatusResponse {

    private List<Status> statuses;
    private String timestamp;

    public StatusResponse(List<Status> statuses, String timestamp) {
        this.statuses = statuses;
        this.timestamp = timestamp;
    }
    public StatusResponse() {
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
}
