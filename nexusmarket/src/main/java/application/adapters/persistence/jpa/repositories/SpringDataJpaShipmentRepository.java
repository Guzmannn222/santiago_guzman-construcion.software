package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Shipment persistence entity. */
public interface SpringDataJpaShipmentRepository extends JpaRepository<ShipmentJpaEntity, Long> {

    Optional<ShipmentJpaEntity> findByOrderId(Long orderId);
}
