package application.domain.models;

import application.domain.exceptions.InvalidWarehouseException;
import application.domain.exceptions.InvalidWarehouseStatusException;
import application.domain.valueobjects.WarehouseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Physical storage space, owned either by the marketplace or by a seller. */
@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

    private Long id;
    private String location;
    private WarehouseType type;
    private boolean active = true;
    /** Set only when type == SELLER; the seller that owns this warehouse. */
    private Seller owner;

    public Warehouse(String location, WarehouseType type, Seller owner) {
        if (location == null || location.isBlank() || location.length() > 200) {
            throw new InvalidWarehouseException("Warehouse location must be provided (max 200 characters).");
        }
        if (type == null) {
            throw new InvalidWarehouseException("Warehouse type must be provided.");
        }
        if (WarehouseType.SELLER.equals(type) && owner == null) {
            throw new InvalidWarehouseException("A SELLER warehouse must have an owner seller.");
        }
        if (WarehouseType.MARKETPLACE.equals(type) && owner != null) {
            throw new InvalidWarehouseException("A MARKETPLACE warehouse cannot have an owner seller.");
        }
        this.location = location.trim();
        this.type = type;
        this.owner = owner;
        this.active = true;
    }

    public void deactivate() {
        if (!active) {
            throw new InvalidWarehouseStatusException("The warehouse is already inactive.");
        }
        this.active = false;
    }

    public void ensureActive() {
        if (!active) {
            throw new InvalidWarehouseStatusException("The warehouse is inactive.");
        }
    }
}
