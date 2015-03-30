package se.inera.monitoring.persistence.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Status implements Comparable<Status> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;
    @Column
    private String service;
    @Column
    private String subservice;
    @Column
    private String status;
    @Column
    private int severity;
    @Column
    private Timestamp timestamp;

    public Status() {
    }

    public Status(String service, String subservice, String status, int severity, Timestamp timestamp) {
        this.service = service;
        this.subservice = subservice;
        this.status = status;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    public String getSubservice() {
        return subservice;
    }

    public void setSubservice(String subservice) {
        this.subservice = subservice;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((service == null) ? 0 : service.hashCode());
        result = prime * result
                + ((subservice == null) ? 0 : subservice.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Status other = (Status) obj;
        if (service == null) {
            if (other.service != null)
                return false;
        } else if (!service.equals(other.service))
            return false;
        if (subservice == null) {
            if (other.subservice != null)
                return false;
        } else if (!subservice.equals(other.subservice))
            return false;
        return true;
    }

    @Override
    public int compareTo(Status o) {
        if (service.equals(o.service))
            return subservice.compareTo(o.subservice);
        else
            return service.compareTo(o.service);
    }
}
