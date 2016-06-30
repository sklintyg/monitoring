package se.inera.monitoring.persistence;

import java.util.List;

import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

public interface ApplicationStatusRepository {

    void save(UserCount userCount, String system);
    void save(StatusResponse status);
    List<UserCount> getUserCount(String system);
    List<StatusResponse> getStatus(String system);

}
