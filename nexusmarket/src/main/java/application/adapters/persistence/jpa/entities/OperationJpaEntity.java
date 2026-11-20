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
 * JPA persistence entity (repository DTO) for the Operation aggregate.
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "operations")
@Getter
@Setter
@NoArgsConstructor
public class OperationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** OperationType catalog code. */
    @Column(name = "operation_type", nullable = false, length = 40)
    private String operationType;

    @Column(name = "execution_date")
    private LocalDateTime executionDate;

    /** Operation.performedBy reference. */
    @Column(name = "performed_by_user_id")
    private Long performedByUserId;

    /** Operation.affectedEntity type (EntityType code). */
    @Column(name = "affected_entity_type", length = 20)
    private String affectedEntityType;

    /** Operation.affectedEntity identifier. */
    @Column(name = "affected_entity_identifier", length = 60)
    private String affectedEntityIdentifier;
}
