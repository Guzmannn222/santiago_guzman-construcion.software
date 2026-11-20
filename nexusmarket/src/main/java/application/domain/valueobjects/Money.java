package application.domain.valueobjects;

import application.domain.exceptions.CurrencyMismatchException;
import application.domain.exceptions.InvalidMoneyException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value Object representing an amount of money in a specific currency.
 *
 * <p>Invariants: the amount and the currency are always present, the amount is never negative and
 * it is always normalized to two decimals ({@link RoundingMode#HALF_UP}). Operations that would
 * mix currencies are rejected. Strictly-positive amounts (prices, refunds) are enforced by the
 * aggregate that owns the amount through {@link #isPositive()}.
 */
@Getter
@EqualsAndHashCode
public final class Money {

    public static final int SCALE = 2;

    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new InvalidMoneyException("Money amount must be provided.");
        }
        if (currency == null) {
            throw new InvalidMoneyException("Money currency must be provided.");
        }
        if (amount.signum() < 0) {
            throw new InvalidMoneyException("Money amount must not be negative.");
        }
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean hasSameCurrency(Money other) {
        return other != null && currency.equals(other.currency);
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(int quantity) {
        if (quantity < 0) {
            throw new InvalidMoneyException("Money cannot be multiplied by a negative quantity.");
        }
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    /** Multiplies by a rate (e.g. a tax rate). The rate must not be negative. */
    public Money multiply(BigDecimal rate) {
        if (rate == null || rate.signum() < 0) {
            throw new InvalidMoneyException("Money can only be multiplied by a non-negative rate.");
        }
        return new Money(amount.multiply(rate), currency);
    }

    private void requireSameCurrency(Money other) {
        if (other == null) {
            throw new InvalidMoneyException("Money operand must be provided.");
        }
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatchException(
                    "Currency mismatch: " + currency.getCode() + " vs " + other.currency.getCode() + ".");
        }
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency.getCode();
    }
}
