package se.inera.monitoring.persistence.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "applicationstatus", type = "ApplicationStatus", shards = 1, replicas = 0, indexStoreType = "memory")
public class ApplicationStatus {

    @Id
    private String id;
    private String application;
    private String version;
    private String server;

    @Field(type = FieldType.Date ,format = DateFormat.basic_date_time)
    private Date timestamp;
    private long responsetime;

    @Field(type = FieldType.Integer, index = FieldIndex.analyzed)
    private Integer currentUsers;

    @Field(type = FieldType.Nested, index = FieldIndex.analyzed)
    private List<SubsystemStatus> subsystemStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getResponsetime() {
        return responsetime;
    }

    public void setResponsetime(long responsetime) {
        this.responsetime = responsetime;
    }

    public Integer getCurrentUsers() {
        return currentUsers;
    }

    public void setCurrentUsers(Integer currentUsers) {
        this.currentUsers = currentUsers;
    }

    public List<SubsystemStatus> getSubsystemStatus() {
        return subsystemStatus;
    }

    public void setSubsystemStatus(List<SubsystemStatus> subsystemStatus) {
        this.subsystemStatus = subsystemStatus;
    }
}