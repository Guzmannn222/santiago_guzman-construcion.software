package application.domain.models;

import application.domain.exceptions.InvalidCartException;
import application.domain.valueobjects.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A single product line inside a {@link Cart}. The unit price is a snapshot taken when added. */
@Getter
@Setter
@NoArgsConstructor
public class CartItem {

    private Product product;
    private int quantity;
    private Money unitPrice;

    public CartItem(Product product, int quantity, Money unitPrice) {
        if (product == null) {
            throw new InvalidCartException("Cart item product must be provided.");
        }
        if (unitPrice == null) {
            throw new InvalidCartException("Cart item unit price must be provided.");
        }
        Cart.requireValidQuantity(quantity);
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Money getLineTotal() {
        return unitPrice.multiply(quantity);
    }
}
