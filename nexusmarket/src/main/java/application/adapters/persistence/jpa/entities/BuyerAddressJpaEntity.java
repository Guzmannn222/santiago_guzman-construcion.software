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
 * JPA persistence entity (repository DTO) for the BuyerAddress aggregate (child rows).
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "buyer_addresses")
@Getter
@Setter
@NoArgsConstructor
public class BuyerAddressJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "address", nullable = false, length = 255)
    private String address;
}
