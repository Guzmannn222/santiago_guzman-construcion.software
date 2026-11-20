package application.domain.services.operation;

import application.domain.exceptions.InvalidAuditLogException;
import application.domain.models.AuditLog;
import application.domain.ports.out.AuditLogRepositoryPort;
import org.springframework.stereotype.Service;

/** Registers an immutable, append-only {@link AuditLog} through the AuditLogRepositoryPort. */
@Service
public class RegisterAuditLogService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public RegisterAuditLogService(AuditLogRepositoryPort auditLogRepositoryPort) {
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public AuditLog execute(AuditLog auditLog) {
        if (auditLog == null) {
            throw new InvalidAuditLogException("Audit log must be provided.");
        }
        if (auditLog.getOperationType() == null) {
            throw new InvalidAuditLogException("Audit log operation type must be provided.");
        }
        if (auditLog.getAffectedEntity() == null) {
            throw new InvalidAuditLogException("Audit log affected entity must be provided.");
        }
        return auditLogRepositoryPort.save(auditLog);
    }
}
