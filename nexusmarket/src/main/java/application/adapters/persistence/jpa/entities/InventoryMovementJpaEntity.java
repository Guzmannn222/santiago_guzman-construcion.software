package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity (repository DTO) for the InventoryMovement aggregate (child rows).
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    /** InventoryMovementType catalog code. */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "movement_date")
    private LocalDateTime date;

    @Column(name = "reason", length = 255)
    private String reason;
}
