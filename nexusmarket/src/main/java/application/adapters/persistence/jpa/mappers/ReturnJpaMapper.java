package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ReturnJpaEntity;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.ReturnItem;
import application.domain.valueobjects.ReturnStatus;
import application.adapters.persistence.jpa.entities.ReturnItemJpaEntity;
import java.util.ArrayList;
import java.util.List;

/** Bidirectional mapper between the Return Domain Model and its JPA entity. */
public final class ReturnJpaMapper {

    private ReturnJpaMapper() {
    }

    public static ReturnJpaEntity toEntity(Return domain) {
        if (domain == null) {
            return null;
        }
        ReturnJpaEntity entity = new ReturnJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrder() != null ? domain.getOrder().getId() : null);
        entity.setReason(domain.getReason());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : null);
        entity.setRequestDate(domain.getRequestDate());
        return entity;
    }

    public static List<ReturnItemJpaEntity> toItemEntities(Long returnId, List<ReturnItem> items) {
        List<ReturnItemJpaEntity> rows = new ArrayList<>();
        if (items == null) {
            return rows;
        }
        for (ReturnItem item : items) {
            ReturnItemJpaEntity row = new ReturnItemJpaEntity();
            row.setReturnId(returnId);
            row.setProductId(item.getProduct().getId());
            row.setQuantity(item.getQuantity());
            rows.add(row);
        }
        return rows;
    }

    public static ReturnItem toItemDomain(ReturnItemJpaEntity row, Product product) {
        ReturnItem item = new ReturnItem();
        item.setProduct(product);
        item.setQuantity(row.getQuantity() == null ? 0 : row.getQuantity());
        return item;
    }

    public static Return toDomain(ReturnJpaEntity entity, Order order) {
        if (entity == null) {
            return null;
        }
        Return domain = new Return();
        domain.setId(entity.getId());
        domain.setOrder(order);
        domain.setReason(entity.getReason());
        domain.setStatus(entity.getStatus() != null ? ReturnStatus.fromCode(entity.getStatus()) : null);
        domain.setRequestDate(entity.getRequestDate());
        return domain;
    }
}
