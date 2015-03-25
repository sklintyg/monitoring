package se.inera.monitoring.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import se.inera.monitoring.persistence.dao.Status;

public interface StatusRepository extends CrudRepository<Status, Long>{

	List<Status> findByService(String service);
}
