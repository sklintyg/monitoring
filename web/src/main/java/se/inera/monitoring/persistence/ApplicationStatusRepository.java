package se.inera.monitoring.persistence;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import se.inera.monitoring.persistence.model.ApplicationStatus;

public interface ApplicationStatusRepository extends ElasticsearchCrudRepository<ApplicationStatus, String> {

    List<ApplicationStatus> findByApplicationOrderByTimestampDesc(String service, Pageable pageable);
}
