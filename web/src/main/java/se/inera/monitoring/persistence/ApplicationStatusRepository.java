package se.inera.monitoring.persistence;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import se.inera.monitoring.persistence.model.ApplicationStatus;

public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Long> {

    List<ApplicationStatus> findByApplicationOrderByTimestampDescServerDesc(String application, Pageable pageable);
    List<ApplicationStatus> findByApplicationAndServerOrderByTimestampDesc(String application, String server, Pageable pageRequest);
}
