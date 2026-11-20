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
 * JPA persistence entity (repository DTO) for the Buyer aggregate.
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "buyers")
@Getter
@Setter
@NoArgsConstructor
public class BuyerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Buyer.user reference. */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "primary_address", nullable = false, length = 255)
    private String primaryAddress;

    /** TradingStatus catalog code. */
    @Column(name = "trading_status", nullable = false, length = 20)
    private String tradingStatus;
}
