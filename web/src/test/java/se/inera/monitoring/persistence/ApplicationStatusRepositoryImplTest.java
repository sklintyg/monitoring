package se.inera.monitoring.persistence;

import static org.junit.Assert.*;

import org.junit.Test;

import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

public class ApplicationStatusRepositoryImplTest {

    @Test
    public void testSaveStatus() {
        final String application = "application";
        ApplicationStatusRepositoryImpl repo = new ApplicationStatusRepositoryImpl();

        StatusResponse status = new StatusResponse();
        status.setApplication(application);
        status.setServer("server");
        repo.save(status);
        assertEquals(1, repo.getStatus(application).size());

        StatusResponse status2 = new StatusResponse();
        status2.setApplication(application);
        status2.setServer("server");
        repo.save(status2);
        assertEquals(1, repo.getStatus(application).size());

        StatusResponse status3 = new StatusResponse();
        status3.setApplication(application);
        status3.setServer("server3");
        repo.save(status3);
        assertEquals(2, repo.getStatus(application).size());
    }

    @Test
    public void testSaveUserCount() {
        final String application = "application";
        ApplicationStatusRepositoryImpl repo = new ApplicationStatusRepositoryImpl();
        for (int i = 0; i < ApplicationStatusRepositoryImpl.MAX_SIZE; i++) {
            UserCount status = new UserCount("", i, "");
            repo.save(status, application);
        }
        UserCount status = new UserCount("", Integer.MAX_VALUE, ""); // Cause overflow
        repo.save(status, application);

        assertEquals(ApplicationStatusRepositoryImpl.MAX_SIZE, repo.getUserCount(application).size());
        assertEquals(1, repo.getUserCount(application).get(0).getCount());
        assertEquals(Integer.MAX_VALUE, repo.getUserCount(application).get(ApplicationStatusRepositoryImpl.MAX_SIZE - 1).getCount());
    }
}
