package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Refund persistence entity. */
public interface SpringDataJpaRefundRepository extends JpaRepository<RefundJpaEntity, Long> {

    Optional<RefundJpaEntity> findByReturnId(Long returnId);
}
