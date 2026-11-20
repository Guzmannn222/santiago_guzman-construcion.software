package application.domain.models;

import application.domain.exceptions.InvalidOrderException;
import application.domain.exceptions.InvalidOrderStateException;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Formal commercial commitment generated from a confirmed cart.
 * Lifecycle: PENDING_PAYMENT -> PAID -> SHIPPED -> DELIVERED -> FINISHED, or CANCELLED from
 * PENDING_PAYMENT / PAID. FINISHED and CANCELLED are terminal: a closed order can never be
 * modified again.
 */
@Getter
@Setter
@NoArgsConstructor
public class Order {

    private Long id;
    private Buyer buyer;
    private List<OrderItem> items = new ArrayList<>();
    private Money total = Money.zero(Currency.COP);
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;
    private LocalDateTime date;
    /** Warehouse where the stock was reserved (and from where the order ships). */
    private Warehouse warehouse;

    public Order(Buyer buyer, Warehouse warehouse) {
        if (buyer == null) {
            throw new InvalidOrderException("Order buyer must be provided.");
        }
        this.buyer = buyer;
        this.warehouse = warehouse;
        this.status = OrderStatus.PENDING_PAYMENT;
        this.date = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new InvalidOrderException("Order item must be provided.");
        }
        if (!OrderStatus.PENDING_PAYMENT.equals(status)) {
            throw new InvalidOrderStateException("Items can only be added while the order is pending payment.");
        }
        this.items.add(item);
        recalculateTotal();
    }

    public Money recalculateTotal() {
        if (items.isEmpty()) {
            this.total = Money.zero(this.total == null ? Currency.COP : this.total.getCurrency());
            return this.total;
        }
        Money sum = Money.zero(items.get(0).getUnitPrice().getCurrency());
        for (OrderItem item : items) {
            sum = sum.add(item.getSubtotal());
        }
        this.total = sum;
        return sum;
    }

    public void confirmPayment() {
        changeStatus(OrderStatus.PAID);
    }

    public void ship() {
        changeStatus(OrderStatus.SHIPPED);
    }

    public void deliver() {
        changeStatus(OrderStatus.DELIVERED);
    }

    /** Digital-only orders are delivered right after payment (no shipment involved). */
    public void deliverDigital() {
        if (hasPhysicalItems()) {
            throw new InvalidOrderStateException("Only orders made of digital products are delivered without shipment.");
        }
        changeStatus(OrderStatus.DELIVERED);
        finish();
    }

    public void finish() {
        changeStatus(OrderStatus.FINISHED);
    }

    /** Cancels the order. Only allowed before it has been shipped. */
    public void cancel() {
        if (!OrderStatus.PENDING_PAYMENT.equals(status) && !OrderStatus.PAID.equals(status)) {
            throw new InvalidOrderStateException(
                    "An order in status " + statusCode() + " cannot be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void changeStatus(OrderStatus target) {
        if (!canModify()) {
            throw new InvalidOrderStateException("The order is " + statusCode() + " and cannot be modified.");
        }
        if (target == null || !isValidTransition(status, target)) {
            throw new InvalidOrderStateException("Invalid order status transition from " + statusCode() + " to "
                    + (target == null ? "null" : target.getCode()) + ".");
        }
        this.status = target;
    }

    /** FINISHED and CANCELLED orders can never be modified. */
    public boolean canModify() {
        return !OrderStatus.FINISHED.equals(status) && !OrderStatus.CANCELLED.equals(status);
    }

    public boolean hasPhysicalItems() {
        return items.stream().anyMatch(item -> item.getProduct().isPhysical());
    }

    /** Orders are eligible for a return only after being delivered. */
    public boolean isDelivered() {
        return OrderStatus.DELIVERED.equals(status) || OrderStatus.FINISHED.equals(status);
    }

    public boolean isPaidOrLater() {
        return OrderStatus.PAID.equals(status) || OrderStatus.SHIPPED.equals(status)
                || OrderStatus.DELIVERED.equals(status) || OrderStatus.FINISHED.equals(status);
    }

    public boolean isOwnedBy(User candidate) {
        return buyer != null && buyer.isOwnedBy(candidate);
    }

    public boolean containsProductOf(Seller seller) {
        return seller != null && items.stream().anyMatch(item -> item.getProduct().belongsTo(seller));
    }

    private String statusCode() {
        return status == null ? "UNKNOWN" : status.getCode();
    }

    private static boolean isValidTransition(OrderStatus current, OrderStatus target) {
        return (OrderStatus.PENDING_PAYMENT.equals(current) && OrderStatus.PAID.equals(target))
                || (OrderStatus.PAID.equals(current) && OrderStatus.SHIPPED.equals(target))
                || (OrderStatus.PAID.equals(current) && OrderStatus.DELIVERED.equals(target))
                || (OrderStatus.SHIPPED.equals(current) && OrderStatus.DELIVERED.equals(target))
                || (OrderStatus.DELIVERED.equals(current) && OrderStatus.FINISHED.equals(target));
    }
}
