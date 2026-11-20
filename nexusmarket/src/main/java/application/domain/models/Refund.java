package application.domain.models;

import application.domain.exceptions.InvalidRefundException;
import application.domain.exceptions.InvalidRefundStatusException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Money reimbursement originated by an approved {@link Return}. The amount can never exceed the
 * order total. Transitions: PENDING -> PROCESSING -> COMPLETED, and PENDING | PROCESSING -> REJECTED.
 */
@Getter
@Setter
@NoArgsConstructor
public class Refund {

    private Long id;
    private Return productReturn;
    private Money amount;
    private RefundStatus status = RefundStatus.PENDING;

    public Refund(Return productReturn, Money amount) {
        if (productReturn == null) {
            throw new InvalidRefundException("Refund return must be provided.");
        }
        if (!productReturn.isRefundable()) {
            throw new InvalidRefundStatusException("Only approved or completed returns can be refunded.");
        }
        if (amount == null || !amount.isPositive()) {
            throw new InvalidRefundException("Refund amount must be greater than zero.");
        }
        Money orderTotal = productReturn.getOrder().getTotal();
        if (!orderTotal.hasSameCurrency(amount)) {
            throw new InvalidRefundException("Refund currency must match the order currency.");
        }
        Money ceiling = productReturn.returnedValue();
        if (amount.isGreaterThan(ceiling)) {
            throw new InvalidRefundException("Refund amount cannot exceed the value of the returned products ("
                    + ceiling + ").");
        }
        this.productReturn = productReturn;
        this.amount = amount;
        this.status = RefundStatus.PENDING;
    }

    public void process() {
        requireStatus(RefundStatus.PENDING, "processed");
        this.status = RefundStatus.PROCESSING;
    }

    public void complete() {
        requireStatus(RefundStatus.PROCESSING, "completed");
        this.status = RefundStatus.COMPLETED;
    }

    public void reject() {
        if (!RefundStatus.PENDING.equals(status) && !RefundStatus.PROCESSING.equals(status)) {
            throw new InvalidRefundStatusException("A refund in status " + status.getCode() + " cannot be rejected.");
        }
        this.status = RefundStatus.REJECTED;
    }

    private void requireStatus(RefundStatus expected, String action) {
        if (!expected.equals(status)) {
            throw new InvalidRefundStatusException("A refund in status " + status.getCode()
                    + " cannot be " + action + " (expected " + expected.getCode() + ").");
        }
    }
}
