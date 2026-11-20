package application.domain.ports.in;

import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import java.util.List;

/** Input Port: consult the audit trail (roles SUPERVISOR, ADMINISTRATOR). */
public interface ConsultAuditLogsUseCase {

    List<AuditLog> consultByUser(User actor, User user);
    List<AuditLog> consultByEntity(User actor, EntityReference entity);
    List<AuditLog> consultByOperationType(User actor, OperationType operationType);
}
