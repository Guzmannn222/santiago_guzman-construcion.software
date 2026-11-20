package application.domain.ports.out;

import application.domain.models.Shipment;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Shipment aggregates. */
public interface ShipmentRepositoryPort {

    Shipment save(Shipment shipment);
    void update(Shipment shipment);
    Optional<Shipment> findById(Long id);
    List<Shipment> findAll();
    Optional<Shipment> findByOrderId(Long orderId);
}
