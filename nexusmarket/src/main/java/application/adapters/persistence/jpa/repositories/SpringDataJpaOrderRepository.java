package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Order persistence entity. */
public interface SpringDataJpaOrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    List<OrderJpaEntity> findByBuyerId(Long buyerId);
}
