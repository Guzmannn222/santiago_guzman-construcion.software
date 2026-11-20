package application.domain.valueobjects;

import java.util.List;

/**
 * Currency in which a Money amount is expressed.
 */
public final class Currency extends DomainCatalog {

    public static final Currency COP = new Currency(
            "COP", "Colombian Peso", "Colombian peso.");
    public static final Currency USD = new Currency(
            "USD", "US Dollar", "United States dollar.");
    public static final Currency EUR = new Currency(
            "EUR", "Euro", "Euro.");

    private static final List<Currency> VALUES = List.of(COP, USD, EUR);

    private Currency(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<Currency> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static Currency fromCode(String code) {
        return lookup("Currency", code, VALUES);
    }
}
