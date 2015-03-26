package se.inera.monitoring.web.domain;

public class UserCount {

    private int count;

    private String timeStamp;

    public UserCount(int count, String timeStamp) {
        super();
        this.count = count;
        this.timeStamp = timeStamp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
