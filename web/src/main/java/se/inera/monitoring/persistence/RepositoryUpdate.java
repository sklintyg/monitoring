package se.inera.monitoring.persistence;

import java.sql.Timestamp;
import java.util.Random;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.inera.monitoring.persistence.dao.Status;
import se.inera.monitoring.persistence.dao.UserCount;

@Service
public class RepositoryUpdate {

	@Autowired
	private StatusRepository statusRepo;

	@Autowired
	private UserCountRepository userRepo;

	@Scheduled(fixedDelay = 5000)
	public void updateDatabases() {
		updateStatus(RepositoryBootstrap.services);
		updateUserCount(RepositoryBootstrap.services);
	}

	private void updateUserCount(String... services) {
		for (String service : services) {
			Random rand = new Random();
			UserCount oldUsercount = userRepo
					.findByServiceOrderByTimestampDesc(service,
							new PageRequest(0, 1)).getContent().get(0);
			DateTime newTime = new DateTime(oldUsercount.getTimestamp())
					.plusMinutes(1);
			int newCount = oldUsercount.getCount()
					+ rand.nextInt(RepositoryBootstrap.maxDiff)
					- RepositoryBootstrap.maxDiff / 2;
			newCount = Math.max(Math.min(newCount, RepositoryBootstrap.max),
					RepositoryBootstrap.min);
			UserCount newUserCount = new UserCount();
			newUserCount.setCount(newCount);
			newUserCount.setService(service);
			newUserCount.setTimestamp(new Timestamp(newTime.getMillis()));
			userRepo.save(newUserCount);
		}
	}

	private void updateStatus(String... services) {
		Random rand = new Random();
		for (String service : services) {
			for (Status status : statusRepo.findByService(service)) {
				boolean isOk = rand.nextBoolean();
				status.setStatus(isOk ? "OK" : "FAIL");
				status.setSeverity(isOk ? 0 : 1);
				statusRepo.save(status);
			}
		}
	}

}
