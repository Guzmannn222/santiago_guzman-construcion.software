package application.domain.services.operation;

import application.domain.exceptions.InvalidOperationException;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.in.ConsultOperationsUseCase;
import application.domain.ports.out.OperationRepositoryPort;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.services.authorization.ValidateUserAuthorizationStatusService;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.SystemRole;
import java.util.List;
import org.springframework.stereotype.Service;

/** Consults the operations log. Read-only; allowed roles: SUPERVISOR, ADMINISTRATOR. */
@Service
public class ConsultOperationsService implements ConsultOperationsUseCase {

    private final OperationRepositoryPort operationRepositoryPort;
    private final ValidateUserAuthorizationStatusService validateStatus;
    private final ValidateRoleAuthorizationService validateRole;

    public ConsultOperationsService(OperationRepositoryPort operationRepositoryPort,
            ValidateUserAuthorizationStatusService validateStatus, ValidateRoleAuthorizationService validateRole) {
        this.operationRepositoryPort = operationRepositoryPort;
        this.validateStatus = validateStatus;
        this.validateRole = validateRole;
    }

    @Override
    public List<Operation> consultByUser(User actor, User user) {
        authorize(actor);
        if (user == null || user.getId() == null) {
            throw new InvalidOperationException("The user to consult must be provided.");
        }
        return operationRepositoryPort.findByUserId(user.getId());
    }

    @Override
    public List<Operation> consultByEntity(User actor, EntityReference entity) {
        authorize(actor);
        if (entity == null) {
            throw new InvalidOperationException("The entity to consult must be provided.");
        }
        return operationRepositoryPort.findByEntity(entity);
    }

    private void authorize(User actor) {
        validateStatus.execute(actor);
        validateRole.execute(actor, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
    }
}
