package se.inera.monitoring.web.domain;

public class Counters {

    private int userCount;
	
    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    @Override
	public String toString() {
		return "Counters [anvandare=" + userCount + "]";
	}
}
