package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OrderItemJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the OrderItem persistence entity. */
public interface SpringDataJpaOrderItemRepository extends JpaRepository<OrderItemJpaEntity, Long> {

    List<OrderItemJpaEntity> findByOrderIdOrderByIdAsc(Long orderId);

    void deleteByOrderId(Long orderId);
}
