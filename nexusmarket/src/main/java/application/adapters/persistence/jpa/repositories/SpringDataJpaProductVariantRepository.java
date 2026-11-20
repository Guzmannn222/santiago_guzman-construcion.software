package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ProductVariantJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the ProductVariant persistence entity. */
public interface SpringDataJpaProductVariantRepository extends JpaRepository<ProductVariantJpaEntity, Long> {

    List<ProductVariantJpaEntity> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}
