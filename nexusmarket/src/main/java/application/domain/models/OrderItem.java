package application.domain.models;

import application.domain.exceptions.InvalidOrderException;
import application.domain.valueobjects.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A single product line inside an {@link Order}. */
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    private Product product;
    private int quantity;
    private Money unitPrice;
    private Money subtotal;

    public OrderItem(Product product, int quantity, Money unitPrice) {
        if (product == null) {
            throw new InvalidOrderException("Order item product must be provided.");
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("Order item quantity must be greater than zero.");
        }
        if (unitPrice == null || !unitPrice.isPositive()) {
            throw new InvalidOrderException("Order item unit price must be greater than zero.");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public Money calculateSubtotal() {
        this.subtotal = unitPrice.multiply(quantity);
        return this.subtotal;
    }
}
