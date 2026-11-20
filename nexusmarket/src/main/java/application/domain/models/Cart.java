package application.domain.models;

import application.domain.exceptions.EmptyCartException;
import application.domain.exceptions.InvalidCartException;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Provisional product selection made by a {@link Buyer} (cardinality 1-1 with its buyer). */
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    public static final int MAX_QUANTITY_PER_ITEM = 100;

    private Long id;
    private Buyer buyer;
    private List<CartItem> items = new ArrayList<>();
    private Money total = Money.zero(Currency.COP);

    public Cart(Buyer buyer) {
        if (buyer == null) {
            throw new InvalidCartException("Cart buyer must be provided.");
        }
        this.buyer = buyer;
    }

    /** Adds a published product; adding an existing product increases its quantity. */
    public void addProduct(Product product, int quantity) {
        if (product == null) {
            throw new InvalidCartException("Product must be provided.");
        }
        requireValidQuantity(quantity);
        product.ensurePurchasable();
        requireSameCurrency(product);
        CartItem existing = find(product);
        if (existing != null) {
            requireValidQuantity(existing.getQuantity() + quantity);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity, product.getPrice()));
        }
        calculateTotal();
    }

    public void removeProduct(Product product) {
        CartItem existing = requireItem(product);
        items.remove(existing);
        calculateTotal();
    }

    /** Sets the new quantity of an item already in the cart (use removeProduct to delete it). */
    public void updateQuantity(Product product, int quantity) {
        CartItem existing = requireItem(product);
        requireValidQuantity(quantity);
        existing.setQuantity(quantity);
        calculateTotal();
    }

    public void clear() {
        items.clear();
        calculateTotal();
    }

    public Money calculateTotal() {
        if (items.isEmpty()) {
            this.total = Money.zero(this.total == null ? Currency.COP : this.total.getCurrency());
            return this.total;
        }
        Money sum = Money.zero(items.get(0).getUnitPrice().getCurrency());
        for (CartItem item : items) {
            sum = sum.add(item.getLineTotal());
        }
        this.total = sum;
        return sum;
    }

    /** Confirms the cart so an {@link Order} can be created from it (CU-09). */
    public void confirm() {
        if (items.isEmpty()) {
            throw new EmptyCartException("The cart must contain at least one product.");
        }
    }

    public boolean isOwnedBy(User candidate) {
        return buyer != null && buyer.isOwnedBy(candidate);
    }

    static void requireValidQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidCartException("Quantity must be greater than zero.");
        }
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            throw new InvalidCartException("Quantity cannot exceed " + MAX_QUANTITY_PER_ITEM + " units per product.");
        }
    }

    private CartItem find(Product product) {
        return items.stream()
                .filter(item -> item.getProduct().getId() != null && item.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(null);
    }

    private CartItem requireItem(Product product) {
        if (product == null) {
            throw new InvalidCartException("Product must be provided.");
        }
        CartItem existing = find(product);
        if (existing == null) {
            throw new InvalidCartException("Product " + product.getId() + " is not in the cart.");
        }
        return existing;
    }

    private void requireSameCurrency(Product product) {
        if (!items.isEmpty() && !items.get(0).getUnitPrice().hasSameCurrency(product.getPrice())) {
            throw new InvalidCartException("All products in a cart must use the same currency.");
        }
    }
}
