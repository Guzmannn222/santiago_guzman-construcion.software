package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import application.domain.models.Seller;
import application.domain.models.User;
import java.util.ArrayList;

/** Bidirectional mapper between the Seller Domain Model and its JPA entity. */
public final class SellerJpaMapper {

    private SellerJpaMapper() {
    }

    public static SellerJpaEntity toEntity(Seller domain) {
        if (domain == null) {
            return null;
        }
        SellerJpaEntity entity = new SellerJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUser() != null ? domain.getUser().getId() : null);
        entity.setBusinessInfo(domain.getBusinessInfo());
        entity.setActive(domain.isActive());
        return entity;
    }

    /** The seller's warehouses are not loaded here (they are queried via WarehouseRepositoryPort.findByOwnerId). */
    public static Seller toDomain(SellerJpaEntity entity, User user) {
        if (entity == null) {
            return null;
        }
        Seller domain = new Seller();
        domain.setId(entity.getId());
        domain.setUser(user);
        domain.setBusinessInfo(entity.getBusinessInfo());
        domain.setActive(Boolean.TRUE.equals(entity.getActive()));
        domain.setWarehouses(new ArrayList<>());
        return domain;
    }
}
