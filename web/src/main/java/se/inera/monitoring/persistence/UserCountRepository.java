package se.inera.monitoring.persistence;

import org.springframework.data.repository.CrudRepository;

import se.inera.monitoring.persistence.dao.UserCount;

public interface UserCountRepository extends CrudRepository<UserCount, String>{

	UserCount findByServiceOrderByTimestampDesc(String service);

}
