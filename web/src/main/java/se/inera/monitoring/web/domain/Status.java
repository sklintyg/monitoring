package se.inera.monitoring.web.domain;

public class Status {

	private String servicename;
	private String statuscode;
	private int severity;

	public Status(String servicename, String statusCode, int severity) {
		this.servicename = servicename;
		this.statuscode = statusCode;
		this.severity = severity;
	}

	public String getServiceName() {
		return servicename;
	}

	public void setServiceName(String servicename) {
		this.servicename = servicename;
	}

	public String getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

}
