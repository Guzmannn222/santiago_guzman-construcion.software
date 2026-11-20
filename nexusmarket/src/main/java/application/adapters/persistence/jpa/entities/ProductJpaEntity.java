package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity (repository DTO) for the Product aggregate.
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", length = 3, nullable = false)
    private String priceCurrency;

    /** ProductType catalog code. */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    /** ProductStatus catalog code. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
