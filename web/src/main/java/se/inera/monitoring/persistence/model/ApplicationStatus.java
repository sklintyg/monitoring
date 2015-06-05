package se.inera.monitoring.persistence.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "APPLICATION_STATUS")
public class ApplicationStatus {

    @Id
    @Column(name = "APPLICATION_STATUS_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "APPLICATION")
    private String application;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "SERVER")
    private String server;

    @Column(name = "TIMESTAMP")
    @Type(type = "timestamp")
    private Timestamp timestamp;

    @Column(name = "RESPONSETIME")
    private long responsetime;

    @Column(name = "CURRENT_USERS")
    private int currentUsers;

    @Column(name = "REACHABLE")
    private boolean reachable = true;

    @OneToMany(mappedBy = "applicationstatus", cascade = CascadeType.ALL)
    private List<SubsystemStatus> subsystemStatuses = new ArrayList<>();

    /**
     * Creates an unreachable ApplicationStatus
     */
    public static ApplicationStatus getUnreachable(String application, String server, Timestamp timestamp) {
        ApplicationStatus unreachable = new ApplicationStatus();
        unreachable.setApplication(application);
        unreachable.setServer(server);
        unreachable.setReachable(false);
        unreachable.setTimestamp(timestamp);
        return unreachable;
    }

    /**
     * Creates an ApplicationStatus from the data in the parameters
     */
    public static ApplicationStatus getApplicationStatus(String serviceName, Integer currentUsers, long responsetime, String server,
            Timestamp timestamp, String version, List<SubsystemStatus> statuses) {
        ApplicationStatus status = new ApplicationStatus();
        status.setApplication(serviceName);
        status.setCurrentUsers(currentUsers);
        status.setResponsetime(responsetime);
        status.setServer(server);
        status.setTimestamp(timestamp);
        status.setVersion(version);
        status.setSubsystemStatuses(statuses);
        return status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public long getResponsetime() {
        return responsetime;
    }

    public void setResponsetime(long responsetime) {
        this.responsetime = responsetime;
    }

    public int getCurrentUsers() {
        return currentUsers;
    }

    public void setCurrentUsers(int currentUsers) {
        this.currentUsers = currentUsers;
    }

    public List<SubsystemStatus> getSubsystemStatuses() {
        return subsystemStatuses;
    }

    public void setSubsystemStatuses(List<SubsystemStatus> subsystemStatuses) {
        for (SubsystemStatus status : subsystemStatuses) {
            status.setApplicationstatus(this);
        }
        this.subsystemStatuses = subsystemStatuses;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}
