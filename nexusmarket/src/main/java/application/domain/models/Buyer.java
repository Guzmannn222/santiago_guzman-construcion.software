package application.domain.models;

import application.domain.exceptions.InvalidBuyerException;
import application.domain.exceptions.InvalidTradingStatusException;
import application.domain.valueobjects.TradingStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Buyer profile and delivery addresses. Business rule: a buyer never manages another buyer's
 * information nor any inventory data.
 */
@Getter
@Setter
@NoArgsConstructor
public class Buyer {

    public static final int MAX_ADDITIONAL_ADDRESSES = 10;

    private Long id;
    private User user;
    private String primaryAddress;
    private List<String> additionalAddresses = new ArrayList<>();
    private TradingStatus tradingStatus = TradingStatus.ACTIVE;
    /** Cardinality 1-1 with the buyer's active cart. */
    private Cart cart;

    public Buyer(User user, String primaryAddress) {
        if (user == null) {
            throw new InvalidBuyerException("Buyer user must be provided.");
        }
        this.user = user;
        this.primaryAddress = validateAddress(primaryAddress);
        this.tradingStatus = TradingStatus.ACTIVE;
    }

    public void addAddress(String address) {
        String clean = validateAddress(address);
        if (clean.equalsIgnoreCase(primaryAddress)
                || additionalAddresses.stream().anyMatch(a -> a.equalsIgnoreCase(clean))) {
            throw new InvalidBuyerException("The address is already registered for this buyer.");
        }
        if (additionalAddresses.size() >= MAX_ADDITIONAL_ADDRESSES) {
            throw new InvalidBuyerException(
                    "A buyer can have at most " + MAX_ADDITIONAL_ADDRESSES + " additional addresses.");
        }
        this.additionalAddresses.add(clean);
    }

    /** Only buyers with ACTIVE commercial status can place orders. */
    public void ensureCanPlaceOrders() {
        if (!TradingStatus.ACTIVE.equals(tradingStatus)) {
            throw new InvalidTradingStatusException("The buyer commercial status is "
                    + (tradingStatus == null ? "UNKNOWN" : tradingStatus.getCode())
                    + " and does not allow placing orders.");
        }
    }

    public boolean isOwnedBy(User candidate) {
        return candidate != null && user != null && user.isSameUserAs(candidate);
    }

    private static String validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidBuyerException("Address must be provided.");
        }
        if (address.length() > 200) {
            throw new InvalidBuyerException("Address must have at most 200 characters.");
        }
        return address.trim();
    }
}
