package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Operation persistence entity. */
public interface SpringDataJpaOperationRepository extends JpaRepository<OperationJpaEntity, Long> {

    List<OperationJpaEntity> findByPerformedByUserId(Long performedByUserId);

    List<OperationJpaEntity> findByAffectedEntityTypeAndAffectedEntityIdentifier(String affectedEntityType, String affectedEntityIdentifier);
}
