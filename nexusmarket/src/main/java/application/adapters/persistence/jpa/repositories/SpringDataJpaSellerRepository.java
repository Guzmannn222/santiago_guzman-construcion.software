package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Seller persistence entity. */
public interface SpringDataJpaSellerRepository extends JpaRepository<SellerJpaEntity, Long> {

    Optional<SellerJpaEntity> findByUserId(Long userId);
}
