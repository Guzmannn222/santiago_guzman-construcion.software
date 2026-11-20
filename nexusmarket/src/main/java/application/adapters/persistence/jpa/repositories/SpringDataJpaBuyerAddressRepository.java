package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.BuyerAddressJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the BuyerAddress persistence entity. */
public interface SpringDataJpaBuyerAddressRepository extends JpaRepository<BuyerAddressJpaEntity, Long> {

    List<BuyerAddressJpaEntity> findByBuyerIdOrderByPositionAsc(Long buyerId);

    void deleteByBuyerId(Long buyerId);
}
