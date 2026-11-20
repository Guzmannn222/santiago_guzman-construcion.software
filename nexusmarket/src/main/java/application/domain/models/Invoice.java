package application.domain.models;

import application.domain.exceptions.InvalidInvoiceException;
import application.domain.exceptions.InvalidInvoiceStatusException;
import application.domain.valueobjects.Money;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Commercial billing information tied to a paid {@link Order}. */
@Getter
@Setter
@NoArgsConstructor
public class Invoice {

    private Long id;
    private Order order;
    private Money subtotal;
    private Money tax;
    private Money total;
    private BigDecimal taxRate;
    private boolean cancelled;
    private LocalDateTime issueDate;

    public Invoice(Order order) {
        if (order == null) {
            throw new InvalidInvoiceException("Invoice order must be provided.");
        }
        this.order = order;
    }

    /** Generates the amounts. The order must be paid and the rate must be between 0 and 1. */
    public void generate(BigDecimal taxRate) {
        if (taxRate == null || taxRate.signum() < 0 || taxRate.compareTo(BigDecimal.ONE) > 0) {
            throw new InvalidInvoiceException("The tax rate must be between 0 and 1.");
        }
        if (!order.isPaidOrLater()) {
            throw new InvalidInvoiceStatusException("An invoice can only be generated for a paid order.");
        }
        this.taxRate = taxRate;
        this.subtotal = order.getTotal();
        this.tax = this.subtotal.multiply(taxRate);
        this.issueDate = LocalDateTime.now();
        calculateTotal();
    }

    public Money calculateTotal() {
        this.total = this.subtotal.add(this.tax);
        return this.total;
    }

    public void cancel() {
        if (cancelled) {
            throw new InvalidInvoiceStatusException("The invoice is already cancelled.");
        }
        this.cancelled = true;
        this.total = Money.zero(this.subtotal.getCurrency());
    }
}
