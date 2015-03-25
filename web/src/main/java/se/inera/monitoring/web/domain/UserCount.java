package se.inera.monitoring.web.domain;

import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class UserCount {

    private int count;

    private String timeStamp;
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime timeStamp;

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
