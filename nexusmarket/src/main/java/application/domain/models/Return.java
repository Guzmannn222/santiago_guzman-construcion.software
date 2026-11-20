package application.domain.models;

import application.domain.exceptions.InvalidReturnException;
import application.domain.exceptions.InvalidReturnStatusException;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ReturnStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Post-sale return request associated with a delivered {@link Order}.
 * Transitions: REQUESTED -> APPROVED | REJECTED, APPROVED -> COMPLETED.
 */
@Getter
@Setter
@NoArgsConstructor
public class Return {

    private Long id;
    private Order order;
    private String reason;
    private ReturnStatus status = ReturnStatus.REQUESTED;
    private LocalDateTime requestDate;
    /** Lines being returned. Empty means "the whole order" (returns created before item-level support). */
    private List<ReturnItem> items = new ArrayList<>();

    public Return(Order order, String reason) {
        this(order, reason, null);
    }

    /**
     * @param requested lines to return; null or empty returns every line of the order. Each product
     *                  must belong to the order, appear once and not exceed the purchased quantity.
     */
    public Return(Order order, String reason, List<ReturnItem> requested) {
        if (order == null) {
            throw new InvalidReturnException("Return order must be provided.");
        }
        if (!order.isDelivered()) {
            throw new InvalidReturnStatusException("Only delivered orders are eligible for a return.");
        }
        if (reason == null || reason.isBlank() || reason.length() > 500) {
            throw new InvalidReturnException("Return reason must be provided (max 500 characters).");
        }
        this.order = order;
        this.reason = reason.trim();
        this.status = ReturnStatus.REQUESTED;
        this.requestDate = LocalDateTime.now();
        this.items = buildItems(order, requested);
    }

    private static List<ReturnItem> buildItems(Order order, List<ReturnItem> requested) {
        List<ReturnItem> result = new ArrayList<>();
        if (requested == null || requested.isEmpty()) {
            for (OrderItem line : order.getItems()) {
                result.add(new ReturnItem(line.getProduct(), line.getQuantity()));
            }
            return result;
        }
        for (ReturnItem wanted : requested) {
            if (wanted == null || wanted.getProduct() == null) {
                throw new InvalidReturnException("Return item product must be provided.");
            }
            Long productId = wanted.getProduct().getId();
            OrderItem line = findLine(order, productId);
            if (line == null) {
                throw new InvalidReturnException("Product " + productId + " is not part of order " + order.getId() + ".");
            }
            for (ReturnItem already : result) {
                if (productId != null && productId.equals(already.getProduct().getId())) {
                    throw new InvalidReturnException("Product " + productId + " appears more than once in the return.");
                }
            }
            if (wanted.getQuantity() <= 0 || wanted.getQuantity() > line.getQuantity()) {
                throw new InvalidReturnException("Return quantity for product " + productId
                        + " must be between 1 and " + line.getQuantity() + ".");
            }
            result.add(new ReturnItem(line.getProduct(), wanted.getQuantity()));
        }
        return result;
    }

    private static OrderItem findLine(Order order, Long productId) {
        for (OrderItem line : order.getItems()) {
            if (productId != null && productId.equals(line.getProduct().getId())) {
                return line;
            }
        }
        return null;
    }

    /** True when at least one returned line is a product of the given seller. */
    public boolean involvesProductOf(Seller seller) {
        if (seller == null) {
            return false;
        }
        for (ReturnItem line : returnedLines()) {
            if (line.getProduct() != null && line.getProduct().belongsTo(seller)) {
                return true;
            }
        }
        return false;
    }

    /** The lines actually being returned; falls back to the whole order when none were stored. */
    public List<ReturnItem> returnedLines() {
        if (items != null && !items.isEmpty()) {
            return items;
        }
        List<ReturnItem> all = new ArrayList<>();
        if (order != null) {
            for (OrderItem line : order.getItems()) {
                all.add(new ReturnItem(line.getProduct(), line.getQuantity()));
            }
        }
        return all;
    }

    /** Value of the returned units at the price paid; the ceiling for the refund. */
    public Money returnedValue() {
        Money total = Money.zero(order != null && order.getTotal() != null ? order.getTotal().getCurrency() : Currency.COP);
        for (ReturnItem line : returnedLines()) {
            OrderItem paid = findLine(order, line.getProduct().getId());
            if (paid != null) {
                total = total.add(paid.getUnitPrice().multiply(line.getQuantity()));
            }
        }
        return total;
    }

    public void approve() {
        requireStatus(ReturnStatus.REQUESTED, "approved");
        this.status = ReturnStatus.APPROVED;
    }

    public void reject() {
        requireStatus(ReturnStatus.REQUESTED, "rejected");
        this.status = ReturnStatus.REJECTED;
    }

    public void complete() {
        requireStatus(ReturnStatus.APPROVED, "completed");
        this.status = ReturnStatus.COMPLETED;
    }

    public boolean isRefundable() {
        return ReturnStatus.APPROVED.equals(status) || ReturnStatus.COMPLETED.equals(status);
    }

    private void requireStatus(ReturnStatus expected, String action) {
        if (!expected.equals(status)) {
            throw new InvalidReturnStatusException("A return in status " + status.getCode()
                    + " cannot be " + action + " (expected " + expected.getCode() + ").");
        }
    }
}
