package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.valueobjects.ShipmentStatus;

/** Bidirectional mapper between the Shipment Domain Model and its JPA entity. */
public final class ShipmentJpaMapper {

    private ShipmentJpaMapper() {
    }

    public static ShipmentJpaEntity toEntity(Shipment domain) {
        if (domain == null) {
            return null;
        }
        ShipmentJpaEntity entity = new ShipmentJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrder() != null ? domain.getOrder().getId() : null);
        entity.setAddress(domain.getAddress());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : null);
        entity.setDispatchDate(domain.getDispatchDate());
        entity.setDeliveryDate(domain.getDeliveryDate());
        return entity;
    }

    public static Shipment toDomain(ShipmentJpaEntity entity, Order order) {
        if (entity == null) {
            return null;
        }
        Shipment domain = new Shipment();
        domain.setId(entity.getId());
        domain.setOrder(order);
        domain.setAddress(entity.getAddress());
        domain.setStatus(entity.getStatus() != null ? ShipmentStatus.fromCode(entity.getStatus()) : null);
        domain.setDispatchDate(entity.getDispatchDate());
        domain.setDeliveryDate(entity.getDeliveryDate());
        return domain;
    }
}
