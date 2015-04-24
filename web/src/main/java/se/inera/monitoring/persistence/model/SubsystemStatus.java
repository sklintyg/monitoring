package se.inera.monitoring.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subsystem_status")
public class SubsystemStatus {

    @Id
    @Column(name = "SUBSYSTEM_STATUS_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "SUBSYSTEM")
    private String subsystem;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "SEVERITY")
    private int severity;

    @ManyToOne
    @JoinColumn(name = "APPLICATION_STATUS_ID", nullable = false)
    private ApplicationStatus applicationstatus;

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ApplicationStatus getApplicationstatus() {
        return applicationstatus;
    }

    public void setApplicationstatus(ApplicationStatus applicationstatus) {
        this.applicationstatus = applicationstatus;
    }
}
