package se.inera.monitoring.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

@Service
public class ApplicationStatusRepositoryImpl implements ApplicationStatusRepository {

    protected final static int MAX_SIZE = 360;
    protected ConcurrentHashMap<String, ArrayList<UserCount>> userCount = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<String, List<StatusResponse>> statusResponse = new ConcurrentHashMap<>();

    @Override
    public List<UserCount> getUserCount(String system) {
        return userCount.get(system);
    }

    @Override
    public List<StatusResponse> getStatus(String system) {
        return statusResponse.get(system);
    }

    @Override
    public void save(UserCount count, String system) {
        ArrayList<UserCount> users = userCount.get(system);

        if (users == null) {
            users = new ArrayList<>();
            userCount.put(system, users);
        } else {
            if (users.size() == MAX_SIZE) {
                users.remove(0); // Removes oldest element
            }
        }
        users.add(users.size(), count);
    }

    @Override
    public void save(StatusResponse status) {
        List<StatusResponse> systemStatus = statusResponse.get(status.getApplication());

        if (systemStatus == null) {
            systemStatus = new ArrayList<>();
            statusResponse.put(status.getApplication(), systemStatus);
        } else {
            systemStatus.removeIf(sr -> sr.getServer().equals(status.getServer()));
        }
        systemStatus.add(status);
    }
}
