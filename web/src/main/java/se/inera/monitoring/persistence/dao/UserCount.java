package se.inera.monitoring.persistence.dao;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.joda.time.DateTime;

@Entity
public class UserCount {
	
	@Id
	private int id;
	
	private int count;
	private DateTime timestamp;
	private String service;

	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public DateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
