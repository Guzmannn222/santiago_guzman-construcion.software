package application.adapters.persistence.mongodb.mappers;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.EntityType;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;

/**
 * Bidirectional mapper between the AuditLog Domain Model and its MongoDB document. The acting
 * user and the affected entity are stored denormalized, so reading an entry never requires
 * loading other aggregates.
 */
public final class AuditLogMongoMapper {

    private AuditLogMongoMapper() {
    }

    public static AuditLogDocument toDocument(AuditLog domain) {
        if (domain == null) {
            return null;
        }
        AuditLogDocument document = new AuditLogDocument();
        document.setAuditId(domain.getAuditId());
        document.setOperationType(domain.getOperationType() != null ? domain.getOperationType().getCode() : null);
        document.setOperationDate(domain.getOperationDate());
        User performedBy = domain.getPerformedBy();
        if (performedBy != null) {
            document.setPerformedByUserId(performedBy.getId());
            document.setPerformedByUsername(performedBy.getUsername());
            document.setPerformedByRole(performedBy.getRole() != null ? performedBy.getRole().getCode() : null);
        }
        document.setUserRole(domain.getUserRole() != null ? domain.getUserRole().getCode() : null);
        EntityReference ref = domain.getAffectedEntity();
        if (ref != null) {
            document.setAffectedEntityType(ref.getType().getCode());
            document.setAffectedEntityIdentifier(ref.getIdentifier());
        }
        document.setDetails(domain.getDetails());
        return document;
    }

    public static AuditLog toDomain(AuditLogDocument document) {
        if (document == null) {
            return null;
        }
        AuditLog domain = new AuditLog();
        domain.setAuditId(document.getAuditId());
        domain.setOperationType(document.getOperationType() != null
                ? OperationType.fromCode(document.getOperationType()) : null);
        domain.setOperationDate(document.getOperationDate());
        domain.setPerformedBy(userReference(document));
        domain.setUserRole(document.getUserRole() != null ? SystemRole.fromCode(document.getUserRole()) : null);
        if (document.getAffectedEntityType() != null && document.getAffectedEntityIdentifier() != null) {
            domain.setAffectedEntity(new EntityReference(EntityType.fromCode(document.getAffectedEntityType()),
                    document.getAffectedEntityIdentifier()));
        }
        domain.setDetails(document.getDetails());
        return domain;
    }

    private static User userReference(AuditLogDocument document) {
        if (document.getPerformedByUserId() == null && document.getPerformedByUsername() == null
                && document.getPerformedByRole() == null) {
            return null;
        }
        User user = new User();
        user.setId(document.getPerformedByUserId());
        user.setUsername(document.getPerformedByUsername());
        user.setRole(document.getPerformedByRole() != null ? SystemRole.fromCode(document.getPerformedByRole()) : null);
        return user;
    }
}
