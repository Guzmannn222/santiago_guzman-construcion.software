package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Invoice persistence entity. */
public interface SpringDataJpaInvoiceRepository extends JpaRepository<InvoiceJpaEntity, Long> {

    Optional<InvoiceJpaEntity> findByOrderId(Long orderId);
}
