package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Buyer persistence entity. */
public interface SpringDataJpaBuyerRepository extends JpaRepository<BuyerJpaEntity, Long> {

    Optional<BuyerJpaEntity> findByUserId(Long userId);
}
