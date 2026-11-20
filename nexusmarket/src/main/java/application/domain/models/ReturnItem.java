package application.domain.models;

import application.domain.exceptions.InvalidReturnException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A product line (product + units) that a buyer sends back inside a {@link Return}. */
@Getter
@Setter
@NoArgsConstructor
public class ReturnItem {

    private Product product;
    private int quantity;

    public ReturnItem(Product product, int quantity) {
        if (product == null) {
            throw new InvalidReturnException("Return item product must be provided.");
        }
        if (quantity <= 0) {
            throw new InvalidReturnException("Return item quantity must be greater than zero.");
        }
        this.product = product;
        this.quantity = quantity;
    }
}
