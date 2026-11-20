package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.CartItemJpaEntity;
import application.adapters.persistence.jpa.entities.CartJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Product;
import java.util.ArrayList;
import java.util.List;

/** Bidirectional mapper between the Cart Domain Model and its JPA entities. */
public final class CartJpaMapper {

    private CartJpaMapper() {
    }

    public static CartJpaEntity toEntity(Cart domain) {
        if (domain == null) {
            return null;
        }
        CartJpaEntity entity = new CartJpaEntity();
        entity.setId(domain.getId());
        entity.setBuyerId(domain.getBuyer() != null ? domain.getBuyer().getId() : null);
        entity.setTotalAmount(MoneyJpaMapper.amount(domain.getTotal()));
        entity.setTotalCurrency(MoneyJpaMapper.currency(domain.getTotal()));
        return entity;
    }

    public static List<CartItemJpaEntity> toItemEntities(Long cartId, List<CartItem> items) {
        List<CartItemJpaEntity> result = new ArrayList<>();
        if (items == null) {
            return result;
        }
        for (CartItem item : items) {
            CartItemJpaEntity row = new CartItemJpaEntity();
            row.setCartId(cartId);
            row.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
            row.setQuantity(item.getQuantity());
            row.setUnitPriceAmount(MoneyJpaMapper.amount(item.getUnitPrice()));
            row.setUnitPriceCurrency(MoneyJpaMapper.currency(item.getUnitPrice()));
            result.add(row);
        }
        return result;
    }

    public static CartItem toItemDomain(CartItemJpaEntity row, Product product) {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(row.getQuantity() != null ? row.getQuantity() : 0);
        item.setUnitPrice(MoneyJpaMapper.toMoney(row.getUnitPriceAmount(), row.getUnitPriceCurrency()));
        return item;
    }

    public static Cart toDomain(CartJpaEntity entity, Buyer buyer, List<CartItem> items) {
        if (entity == null) {
            return null;
        }
        Cart domain = new Cart();
        domain.setId(entity.getId());
        domain.setBuyer(buyer);
        domain.setItems(items != null ? new ArrayList<>(items) : new ArrayList<>());
        domain.setTotal(MoneyJpaMapper.toMoney(entity.getTotalAmount(), entity.getTotalCurrency()));
        return domain;
    }
}
