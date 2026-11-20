package application.domain.ports.out;

import application.domain.models.AuditLog;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import java.util.List;

/** Output port: immutable audit trail (MongoDB, append-only). */
public interface AuditLogRepositoryPort {

    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByEntity(EntityReference entity);
    List<AuditLog> findByOperationType(OperationType operationType);
}
