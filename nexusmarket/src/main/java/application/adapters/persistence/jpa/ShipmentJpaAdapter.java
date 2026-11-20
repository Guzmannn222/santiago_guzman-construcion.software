package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.adapters.persistence.jpa.mappers.ShipmentJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaShipmentRepository;
import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Output persistence adapter for Shipment backed by Spring Data JPA (MySQL). */
@Repository
public class ShipmentJpaAdapter implements ShipmentRepositoryPort {

    private final SpringDataJpaShipmentRepository repository;
    private final OrderRepositoryPort orderRepositoryPort;

    public ShipmentJpaAdapter(SpringDataJpaShipmentRepository repository, OrderRepositoryPort orderRepositoryPort) {
        this.repository = repository;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Shipment save(Shipment shipment) {
        if (shipment == null) {
            return null;
        }
        return toDomain(repository.save(ShipmentJpaMapper.toEntity(shipment)));
    }

    @Override
    public void update(Shipment shipment) {
        if (shipment != null) {
            repository.save(ShipmentJpaMapper.toEntity(shipment));
        }
    }

    @Override
    public Optional<Shipment> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Shipment> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Shipment> findByOrderId(Long orderId) {
        return orderId == null ? Optional.empty() : repository.findByOrderId(orderId).map(this::toDomain);
    }

    private Shipment toDomain(ShipmentJpaEntity entity) {
        return ShipmentJpaMapper.toDomain(entity, orderRepositoryPort.findById(entity.getOrderId()).orElse(null));
    }
}
