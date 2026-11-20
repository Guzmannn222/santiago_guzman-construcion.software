package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.CartJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Cart persistence entity. */
public interface SpringDataJpaCartRepository extends JpaRepository<CartJpaEntity, Long> {

    Optional<CartJpaEntity> findByBuyerId(Long buyerId);
}
