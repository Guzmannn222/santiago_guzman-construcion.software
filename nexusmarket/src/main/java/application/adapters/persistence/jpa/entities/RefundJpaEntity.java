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
 * JPA persistence entity (repository DTO) for the Refund aggregate.
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
public class RefundJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "return_id", nullable = false, unique = true)
    private Long returnId;

    @Column(name = "amount_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amountAmount;

    @Column(name = "amount_currency", length = 3, nullable = false)
    private String amountCurrency;

    /** RefundStatus catalog code. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
