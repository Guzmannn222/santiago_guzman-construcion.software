package application.domain.services.operation;

import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.ports.in.ConsultAuditLogsUseCase;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.services.authorization.ValidateUserAuthorizationStatusService;
import application.domain.exceptions.InvalidAuditLogException;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.util.List;
import org.springframework.stereotype.Service;

/** Consults the audit trail. Read-only; allowed roles: SUPERVISOR, ADMINISTRATOR. */
@Service
public class ConsultAuditLogsService implements ConsultAuditLogsUseCase {

    private final AuditLogRepositoryPort auditLogRepositoryPort;
    private final ValidateUserAuthorizationStatusService validateStatus;
    private final ValidateRoleAuthorizationService validateRole;

    public ConsultAuditLogsService(AuditLogRepositoryPort auditLogRepositoryPort,
            ValidateUserAuthorizationStatusService validateStatus, ValidateRoleAuthorizationService validateRole) {
        this.auditLogRepositoryPort = auditLogRepositoryPort;
        this.validateStatus = validateStatus;
        this.validateRole = validateRole;
    }

    @Override
    public List<AuditLog> consultByUser(User actor, User user) {
        authorize(actor);
        if (user == null || user.getId() == null) {
            throw new InvalidAuditLogException("The user to consult must be provided.");
        }
        return auditLogRepositoryPort.findByUserId(user.getId());
    }

    @Override
    public List<AuditLog> consultByEntity(User actor, EntityReference entity) {
        authorize(actor);
        if (entity == null) {
            throw new InvalidAuditLogException("The entity to consult must be provided.");
        }
        return auditLogRepositoryPort.findByEntity(entity);
    }

    @Override
    public List<AuditLog> consultByOperationType(User actor, OperationType operationType) {
        authorize(actor);
        if (operationType == null) {
            throw new InvalidAuditLogException("The operation type to consult must be provided.");
        }
        return auditLogRepositoryPort.findByOperationType(operationType);
    }

    private void authorize(User actor) {
        validateStatus.execute(actor);
        validateRole.execute(actor, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
    }
}
