package se.inera.monitoring.web.domain;

import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class UserCount {

    private int count;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;

    public UserCount(int count, LocalDateTime timeStamp) {
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

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}
