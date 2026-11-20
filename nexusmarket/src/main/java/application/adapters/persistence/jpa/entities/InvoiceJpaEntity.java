package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity (repository DTO) for the Invoice aggregate.
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "subtotal_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal subtotalAmount;

    @Column(name = "subtotal_currency", length = 3, nullable = false)
    private String subtotalCurrency;

    @Column(name = "tax_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal taxAmount;

    @Column(name = "tax_currency", length = 3, nullable = false)
    private String taxCurrency;

    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "total_currency", length = 3, nullable = false)
    private String totalCurrency;

    @Column(name = "tax_rate", precision = 5, scale = 4)
    private BigDecimal taxRate;

    @Column(name = "cancelled", nullable = false)
    private Boolean cancelled;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;
}
