package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the Product persistence entity. */
public interface SpringDataJpaProductRepository extends JpaRepository<ProductJpaEntity, Long> {

    List<ProductJpaEntity> findByStatus(String status);

    List<ProductJpaEntity> findBySellerId(Long sellerId);
}
