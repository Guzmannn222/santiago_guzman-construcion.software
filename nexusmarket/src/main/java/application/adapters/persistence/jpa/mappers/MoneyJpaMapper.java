package application.adapters.persistence.jpa.mappers;

import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import java.math.BigDecimal;

/** Flattens the Money value object into amount + currency columns and rebuilds it. */
public final class MoneyJpaMapper {

    private MoneyJpaMapper() {
    }

    public static BigDecimal amount(Money money) {
        return money != null ? money.getAmount() : null;
    }

    public static String currency(Money money) {
        return money != null && money.getCurrency() != null ? money.getCurrency().getCode() : null;
    }

    public static Money toMoney(BigDecimal amount, String currencyCode) {
        if (amount == null || currencyCode == null) {
            return null;
        }
        return Money.of(amount, Currency.fromCode(currencyCode));
    }
}
