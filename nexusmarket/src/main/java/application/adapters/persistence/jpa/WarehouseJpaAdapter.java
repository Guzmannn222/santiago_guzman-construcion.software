package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.adapters.persistence.jpa.mappers.WarehouseJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaWarehouseRepository;
import application.domain.models.Warehouse;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Output persistence adapter for Warehouse backed by Spring Data JPA (MySQL). */
@Repository
public class WarehouseJpaAdapter implements WarehouseRepositoryPort {

    private final SpringDataJpaWarehouseRepository warehouseRepository;
    private final SellerRepositoryPort sellerRepositoryPort;

    public WarehouseJpaAdapter(SpringDataJpaWarehouseRepository warehouseRepository,
                               SellerRepositoryPort sellerRepositoryPort) {
        this.warehouseRepository = warehouseRepository;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        return toDomain(warehouseRepository.save(WarehouseJpaMapper.toEntity(warehouse)));
    }

    @Override
    public void update(Warehouse warehouse) {
        if (warehouse != null) {
            warehouseRepository.save(WarehouseJpaMapper.toEntity(warehouse));
        }
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        return id == null ? Optional.empty() : warehouseRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Warehouse> findByOwnerId(Long sellerId) {
        return sellerId == null ? List.of()
                : warehouseRepository.findByOwnerId(sellerId).stream().map(this::toDomain).toList();
    }

    private Warehouse toDomain(WarehouseJpaEntity entity) {
        return WarehouseJpaMapper.toDomain(entity, sellerRepositoryPort.findById(entity.getOwnerId()).orElse(null));
    }
}
