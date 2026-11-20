package application.domain.models;

import application.domain.exceptions.InvalidInventoryException;
import application.domain.valueobjects.InventoryMovementType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Records a change of stock over an {@link Inventory} record. */
@Getter
@Setter
@NoArgsConstructor
public class InventoryMovement {

    private InventoryMovementType type;
    private int quantity;
    private LocalDateTime date;
    private String reason;

    public InventoryMovement(InventoryMovementType type, int quantity, String reason) {
        if (type == null) {
            throw new InvalidInventoryException("Movement type must be provided.");
        }
        boolean allowsZero = InventoryMovementType.ADJUSTMENT.equals(type);
        if (quantity < 0 || (quantity == 0 && !allowsZero)) {
            throw new InvalidInventoryException("Movement quantity must be greater than zero.");
        }
        if (reason != null && reason.length() > 255) {
            throw new InvalidInventoryException("Movement reason must have at most 255 characters.");
        }
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.date = LocalDateTime.now();
    }
}
