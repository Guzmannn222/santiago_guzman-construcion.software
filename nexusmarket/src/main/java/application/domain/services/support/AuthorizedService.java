package application.domain.services.support;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.models.User;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.services.authorization.ValidateUserAuthorizationStatusService;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.EntityType;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Shared behaviour of the business services: RG-01 (active actor), RG-02 (role check), reload of
 * entities received by reference and traceability registration. It is not a Spring bean itself.
 */
public abstract class AuthorizedService {

    protected final ValidateUserAuthorizationStatusService validateStatus;
    protected final ValidateRoleAuthorizationService validateRole;
    protected final RegisterOperationAndAuditService traceability;

    protected AuthorizedService(ValidateUserAuthorizationStatusService validateStatus,
            ValidateRoleAuthorizationService validateRole, RegisterOperationAndAuditService traceability) {
        this.validateStatus = validateStatus;
        this.validateRole = validateRole;
        this.traceability = traceability;
    }

    /** Actor must be ACTIVE and hold one of the roles. */
    protected void authorize(User actor, SystemRole... roles) {
        validateStatus.execute(actor);
        validateRole.execute(actor, roles);
    }

    protected static <T> T provided(T value, String name) {
        if (value == null) {
            throw new InvalidOperationException(name + " must be provided.");
        }
        return value;
    }

    /** Reloads the authoritative persisted entity; a null reference or id yields "not found". */
    protected static <T> T found(Optional<T> optional, String entity, Long id) {
        return optional.orElseThrow(() -> new EntityNotFoundException(entity, id));
    }

    protected static Long idOf(Long id, String entity) {
        if (id == null) {
            throw new EntityNotFoundException(entity);
        }
        return id;
    }

    protected void register(User actor, OperationType type, EntityType entityType, Long entityId,
            Object... details) {
        traceability.execute(actor, type, EntityReference.of(entityType, entityId), details(details));
    }

    protected static Map<String, Object> details(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            map.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
        }
        return map;
    }
}
