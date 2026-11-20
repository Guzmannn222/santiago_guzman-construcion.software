package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import application.adapters.persistence.jpa.mappers.InventoryJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInventoryMovementRepository;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInventoryRepository;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Output persistence adapter for Inventory backed by Spring Data JPA (MySQL). */
@Repository
public class InventoryJpaAdapter implements InventoryRepositoryPort {

    private final SpringDataJpaInventoryRepository inventoryRepository;
    private final SpringDataJpaInventoryMovementRepository movementRepository;
    private final ProductRepositoryPort productRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public InventoryJpaAdapter(SpringDataJpaInventoryRepository inventoryRepository,
                               SpringDataJpaInventoryMovementRepository movementRepository,
                               ProductRepositoryPort productRepositoryPort,
                               WarehouseRepositoryPort warehouseRepositoryPort) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.productRepositoryPort = productRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    @Override
    @Transactional
    public Inventory save(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        InventoryJpaEntity saved = inventoryRepository.save(InventoryJpaMapper.toEntity(inventory));
        movementRepository.deleteByInventoryId(saved.getId());
        movementRepository.saveAll(InventoryJpaMapper.toMovementEntities(saved.getId(), inventory.getMovements()));
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void update(Inventory inventory) {
        save(inventory);
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return id == null ? Optional.empty() : inventoryRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse) {
        if (product == null || product.getId() == null || warehouse == null || warehouse.getId() == null) {
            return Optional.empty();
        }
        return inventoryRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .map(this::toDomain);
    }

    @Override
    public List<Inventory> findByProductId(Long productId) {
        return productId == null ? List.of()
                : inventoryRepository.findByProductId(productId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Inventory> findByWarehouseId(Long warehouseId) {
        return warehouseId == null ? List.of()
                : inventoryRepository.findByWarehouseId(warehouseId).stream().map(this::toDomain).toList();
    }

    private Inventory toDomain(InventoryJpaEntity entity) {
        return InventoryJpaMapper.toDomain(entity,
                productRepositoryPort.findById(entity.getProductId()).orElse(null),
                warehouseRepositoryPort.findById(entity.getWarehouseId()).orElse(null),
                movementRepository.findByInventoryIdOrderByIdAsc(entity.getId()));
    }
}
