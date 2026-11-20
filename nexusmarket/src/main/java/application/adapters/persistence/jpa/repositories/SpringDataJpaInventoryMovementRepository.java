package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the InventoryMovement persistence entity. */
public interface SpringDataJpaInventoryMovementRepository extends JpaRepository<InventoryMovementJpaEntity, Long> {

    List<InventoryMovementJpaEntity> findByInventoryIdOrderByIdAsc(Long inventoryId);

    void deleteByInventoryId(Long inventoryId);
}
