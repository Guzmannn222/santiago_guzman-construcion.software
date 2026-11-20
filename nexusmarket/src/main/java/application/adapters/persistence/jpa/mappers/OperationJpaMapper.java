package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.EntityType;
import application.domain.valueobjects.OperationType;

/**
 * Bidirectional mapper between the Operation Domain Model and its JPA entity. The affected
 * entity reference is flattened into a type code plus an identifier.
 */
public final class OperationJpaMapper {

    private OperationJpaMapper() {
    }

    public static OperationJpaEntity toEntity(Operation domain) {
        if (domain == null) {
            return null;
        }
        OperationJpaEntity entity = new OperationJpaEntity();
        entity.setId(domain.getOperationId());
        entity.setOperationType(domain.getOperationType() != null ? domain.getOperationType().getCode() : null);
        entity.setExecutionDate(domain.getExecutionDate());
        entity.setPerformedByUserId(domain.getPerformedBy() != null ? domain.getPerformedBy().getId() : null);
        EntityReference ref = domain.getAffectedEntity();
        entity.setAffectedEntityType(ref != null ? ref.getType().getCode() : null);
        entity.setAffectedEntityIdentifier(ref != null ? ref.getIdentifier() : null);
        return entity;
    }

    public static Operation toDomain(OperationJpaEntity entity, User performedBy) {
        if (entity == null) {
            return null;
        }
        Operation domain = new Operation();
        domain.setOperationId(entity.getId());
        domain.setOperationType(entity.getOperationType() != null ? OperationType.fromCode(entity.getOperationType()) : null);
        domain.setExecutionDate(entity.getExecutionDate());
        domain.setPerformedBy(performedBy);
        if (entity.getAffectedEntityType() != null && entity.getAffectedEntityIdentifier() != null) {
            domain.setAffectedEntity(new EntityReference(EntityType.fromCode(entity.getAffectedEntityType()),
                    entity.getAffectedEntityIdentifier()));
        }
        return domain;
    }
}
