package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.OrderItemJpaEntity;
import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.valueobjects.OrderStatus;
import java.util.ArrayList;
import java.util.List;

/** Bidirectional mapper between the Order Domain Model and its JPA entities. */
public final class OrderJpaMapper {

    private OrderJpaMapper() {
    }

    public static OrderJpaEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(domain.getId());
        entity.setBuyerId(domain.getBuyer() != null ? domain.getBuyer().getId() : null);
        entity.setWarehouseId(domain.getWarehouse() != null ? domain.getWarehouse().getId() : null);
        entity.setTotalAmount(MoneyJpaMapper.amount(domain.getTotal()));
        entity.setTotalCurrency(MoneyJpaMapper.currency(domain.getTotal()));
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : null);
        entity.setDate(domain.getDate());
        return entity;
    }

    public static List<OrderItemJpaEntity> toItemEntities(Long orderId, List<OrderItem> items) {
        List<OrderItemJpaEntity> result = new ArrayList<>();
        if (items == null) {
            return result;
        }
        for (OrderItem item : items) {
            OrderItemJpaEntity row = new OrderItemJpaEntity();
            row.setOrderId(orderId);
            row.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
            row.setQuantity(item.getQuantity());
            row.setUnitPriceAmount(MoneyJpaMapper.amount(item.getUnitPrice()));
            row.setUnitPriceCurrency(MoneyJpaMapper.currency(item.getUnitPrice()));
            row.setSubtotalAmount(MoneyJpaMapper.amount(item.getSubtotal()));
            row.setSubtotalCurrency(MoneyJpaMapper.currency(item.getSubtotal()));
            result.add(row);
        }
        return result;
    }

    public static OrderItem toItemDomain(OrderItemJpaEntity row, Product product) {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(row.getQuantity() != null ? row.getQuantity() : 0);
        item.setUnitPrice(MoneyJpaMapper.toMoney(row.getUnitPriceAmount(), row.getUnitPriceCurrency()));
        item.setSubtotal(MoneyJpaMapper.toMoney(row.getSubtotalAmount(), row.getSubtotalCurrency()));
        return item;
    }

    public static Order toDomain(OrderJpaEntity entity, Buyer buyer, Warehouse warehouse, List<OrderItem> items) {
        if (entity == null) {
            return null;
        }
        Order domain = new Order();
        domain.setId(entity.getId());
        domain.setBuyer(buyer);
        domain.setWarehouse(warehouse);
        domain.setItems(items != null ? new ArrayList<>(items) : new ArrayList<>());
        domain.setTotal(MoneyJpaMapper.toMoney(entity.getTotalAmount(), entity.getTotalCurrency()));
        domain.setStatus(entity.getStatus() != null ? OrderStatus.fromCode(entity.getStatus()) : null);
        domain.setDate(entity.getDate());
        return domain;
    }
}
