package se.inera.monitoring.persistence;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

import se.inera.monitoring.persistence.model.UserCount;

public interface UserCountRepository extends CrudRepository<UserCount, Long> {

    List<UserCount> findTop100ByServiceOrderByTimestampDesc(String service);

    Slice<UserCount> findByServiceOrderByTimestampDesc(String service, Pageable pageable);

}
