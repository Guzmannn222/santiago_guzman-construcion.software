package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Warehouse persistence entity. */
public interface SpringDataJpaWarehouseRepository extends JpaRepository<WarehouseJpaEntity, Long> {

    List<WarehouseJpaEntity> findByOwnerId(Long ownerId);
}
