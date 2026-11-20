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
 * JPA persistence entity (repository DTO) for the Warehouse aggregate.
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class WarehouseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "location", nullable = false, length = 200)
    private String location;

    /** WarehouseType catalog code. */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "active", nullable = false)
    private Boolean active;

    /** Warehouse.owner (Seller) reference. */
    @Column(name = "owner_id")
    private Long ownerId;
}
