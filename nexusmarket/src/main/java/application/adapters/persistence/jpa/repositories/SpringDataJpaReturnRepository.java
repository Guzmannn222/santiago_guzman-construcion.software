package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ReturnJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Return persistence entity. */
public interface SpringDataJpaReturnRepository extends JpaRepository<ReturnJpaEntity, Long> {

    List<ReturnJpaEntity> findByOrderId(Long orderId);
}
