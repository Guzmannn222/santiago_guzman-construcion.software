package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity (repository DTO) for the Inventory aggregate.
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
public class InventoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    /** InventoryStatus catalog code. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold;
}
