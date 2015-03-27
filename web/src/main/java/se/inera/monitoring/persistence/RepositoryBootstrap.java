package se.inera.monitoring.persistence;

import java.sql.Timestamp;
import java.util.Random;

import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.dao.Status;
import se.inera.monitoring.persistence.dao.UserCount;

@Service
public class RepositoryBootstrap implements InitializingBean {

    public static int min = 0;
    public static int max = 1000;
    public static int maxDiff = 2 * 1000 / 10;
    public static final String[] services = { "webcert", "minaintyg", "statistik" };

    @Autowired
    private UserCountRepository userCountRepo;

    @Autowired
    private StatusRepository statusRepo;

    @Override
    public void afterPropertiesSet() throws Exception {
        generateUserData(services);
        generateStatusData("minaintyg", "statistik");
    }

    private void generateStatusData(String... services) {
        Random rand = new Random();
        for (String service : services) {
            for (int i = 0; i < 5; i++) {
                boolean isOk = rand.nextBoolean();
                statusRepo.save(new Status(service, "test" + i, isOk ? "OK"
                        : "FAIL", isOk ? 0 : 1));
            }
        }
    }

    private void generateUserData(String... services) {
        Random rand = new Random();
        for (String service : services) {
            int last = rand.nextInt(max - min) + min;
            DateTime time = DateTime.now();

            for (int i = 0; i < 1000; i++) {
                UserCount userCount = new UserCount();
                last = last + rand.nextInt(maxDiff) - maxDiff / 2;
                last = Math.max(Math.min(last, max), min);
                time = time.plusMinutes(1);
                userCount.setCount(last);
                userCount.setService(service);
                userCount.setTimestamp(new Timestamp(time.getMillis()));
                userCountRepo.save(userCount);
            }
        }
    }
}
