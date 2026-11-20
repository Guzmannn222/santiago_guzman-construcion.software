package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ReturnItemJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository for the ReturnItem persistence entity. */
public interface SpringDataJpaReturnItemRepository extends JpaRepository<ReturnItemJpaEntity, Long> {

    List<ReturnItemJpaEntity> findByReturnIdOrderByIdAsc(Long returnId);

    void deleteByReturnId(Long returnId);
}
