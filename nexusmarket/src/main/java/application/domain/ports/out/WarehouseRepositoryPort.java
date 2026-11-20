package application.domain.ports.out;

import application.domain.models.Warehouse;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Warehouse aggregates. */
public interface WarehouseRepositoryPort {

    Warehouse save(Warehouse warehouse);
    void update(Warehouse warehouse);
    Optional<Warehouse> findById(Long id);
    List<Warehouse> findAll();
    List<Warehouse> findByOwnerId(Long sellerId);
}
