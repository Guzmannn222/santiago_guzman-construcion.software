package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseType;

/** Bidirectional mapper between the Warehouse Domain Model and its JPA entity. */
public final class WarehouseJpaMapper {

    private WarehouseJpaMapper() {
    }

    public static WarehouseJpaEntity toEntity(Warehouse domain) {
        if (domain == null) {
            return null;
        }
        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        entity.setId(domain.getId());
        entity.setLocation(domain.getLocation());
        entity.setType(domain.getType() != null ? domain.getType().getCode() : null);
        entity.setActive(domain.isActive());
        entity.setOwnerId(domain.getOwner() != null ? domain.getOwner().getId() : null);
        return entity;
    }

    public static Warehouse toDomain(WarehouseJpaEntity entity, Seller owner) {
        if (entity == null) {
            return null;
        }
        Warehouse domain = new Warehouse();
        domain.setId(entity.getId());
        domain.setLocation(entity.getLocation());
        domain.setType(entity.getType() != null ? WarehouseType.fromCode(entity.getType()) : null);
        domain.setActive(Boolean.TRUE.equals(entity.getActive()));
        domain.setOwner(owner);
        return domain;
    }
}
