package application.domain.ports.out;

import application.domain.models.Return;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Return aggregates. */
public interface ReturnRepositoryPort {

    Return save(Return productReturn);
    void update(Return productReturn);
    Optional<Return> findById(Long id);
    List<Return> findAll();
    List<Return> findByOrderId(Long orderId);
}
