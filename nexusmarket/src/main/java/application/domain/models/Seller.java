package application.domain.models;

import application.domain.exceptions.InvalidSellerException;
import application.domain.exceptions.InvalidSellerStatusException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Product provider. Business rule: sellers cannot self-register; they are onboarded by an
 * Administrator together with their first warehouse (CU-03).
 */
@Getter
@Setter
@NoArgsConstructor
public class Seller {

    private Long id;
    private User user;
    private String businessInfo;
    private boolean active = true;
    /** Warehouses owned by the seller (1..0..*). */
    private List<Warehouse> warehouses = new ArrayList<>();

    public Seller(User user, String businessInfo) {
        if (user == null) {
            throw new InvalidSellerException("Seller user must be provided.");
        }
        if (businessInfo == null || businessInfo.isBlank() || businessInfo.length() > 255) {
            throw new InvalidSellerException("Seller business info must be provided (max 255 characters).");
        }
        this.user = user;
        this.businessInfo = businessInfo.trim();
        this.active = true;
    }

    public void addWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new InvalidSellerException("Warehouse must be provided.");
        }
        this.warehouses.add(warehouse);
    }

    public void deactivate() {
        if (!active) {
            throw new InvalidSellerStatusException("The seller is already inactive.");
        }
        this.active = false;
    }

    public void reactivate() {
        if (active) {
            throw new InvalidSellerStatusException("The seller is already active.");
        }
        this.active = true;
    }

    public void ensureActive() {
        if (!active) {
            throw new InvalidSellerStatusException("The seller is inactive and cannot perform this operation.");
        }
    }

    public boolean isOwnedBy(User candidate) {
        return candidate != null && user != null && user.isSameUserAs(candidate);
    }
}
