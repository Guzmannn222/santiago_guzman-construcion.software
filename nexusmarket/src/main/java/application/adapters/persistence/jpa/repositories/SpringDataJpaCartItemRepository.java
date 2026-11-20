package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.CartItemJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the CartItem persistence entity. */
public interface SpringDataJpaCartItemRepository extends JpaRepository<CartItemJpaEntity, Long> {

    List<CartItemJpaEntity> findByCartIdOrderByIdAsc(Long cartId);

    void deleteByCartId(Long cartId);
}
