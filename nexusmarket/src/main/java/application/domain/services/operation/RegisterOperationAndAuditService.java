package application.domain.services.operation;

import application.domain.models.AuditLog;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Single traceability registration point: every state-changing service calls it after finishing
 * its business action. It records the {@link Operation} (MySQL) and the {@link AuditLog} (MongoDB)
 * in that order and propagates any failure; the domain never touches the databases directly.
 */
@Service
public class RegisterOperationAndAuditService {

    private final RegisterOperationService registerOperationService;
    private final RegisterAuditLogService registerAuditLogService;

    public RegisterOperationAndAuditService(RegisterOperationService registerOperationService,
            RegisterAuditLogService registerAuditLogService) {
        this.registerOperationService = registerOperationService;
        this.registerAuditLogService = registerAuditLogService;
    }

    /** Convenience entry point used by the business services. {@code actor} may be null for system actions. */
    public Operation execute(User actor, OperationType type, EntityReference entity, Map<String, Object> details) {
        Operation operation = new Operation();
        operation.setOperationType(type);
        operation.setExecutionDate(LocalDateTime.now());
        operation.setPerformedBy(actor);
        operation.setAffectedEntity(entity);
        return execute(operation, details);
    }

    public Operation execute(Operation operation, Map<String, Object> auditDetails) {
        Operation persisted = registerOperationService.execute(operation);

        AuditLog auditLog = new AuditLog();
        auditLog.setOperationType(operation.getOperationType());
        auditLog.setOperationDate(operation.getExecutionDate() != null ? operation.getExecutionDate() : LocalDateTime.now());
        auditLog.setPerformedBy(operation.getPerformedBy());
        auditLog.setUserRole(operation.getPerformedBy() != null ? operation.getPerformedBy().getRole() : null);
        auditLog.setAffectedEntity(operation.getAffectedEntity());
        auditLog.setDetails(auditDetails == null ? new HashMap<>() : new HashMap<>(auditDetails));
        registerAuditLogService.execute(auditLog);
        return persisted;
    }
}
