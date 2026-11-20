package application.adapters.persistence.mongodb;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import application.adapters.persistence.mongodb.mappers.AuditLogMongoMapper;
import application.adapters.persistence.mongodb.repositories.AuditLogMongoRepository;
import application.domain.models.AuditLog;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Output persistence adapter for the audit trail backed by Spring Data MongoDB (audit_db,
 * audit_logs). Append-only: only save and finders are exposed.
 */
@Repository
public class AuditLogMongoAdapter implements AuditLogRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(AuditLogMongoAdapter.class);

    private final AuditLogMongoRepository auditLogRepository;

    public AuditLogMongoAdapter(AuditLogMongoRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        AuditLogDocument saved = auditLogRepository.save(AuditLogMongoMapper.toDocument(auditLog));
        log.debug("Audit event persisted: type={} auditId={}", saved.getOperationType(), saved.getAuditId());
        return AuditLogMongoMapper.toDomain(saved);
    }

    @Override
    public List<AuditLog> findByUserId(Long userId) {
        return userId == null ? List.of() : auditLogRepository.findByPerformedByUserId(userId).stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByEntity(EntityReference entity) {
        if (entity == null) {
            return List.of();
        }
        return auditLogRepository.findByAffectedEntityTypeAndAffectedEntityIdentifier(
                entity.getType().getCode(), entity.getIdentifier()).stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByOperationType(OperationType operationType) {
        return operationType == null ? List.of() : auditLogRepository.findByOperationType(operationType.getCode())
                .stream().map(AuditLogMongoMapper::toDomain).toList();
    }
}
