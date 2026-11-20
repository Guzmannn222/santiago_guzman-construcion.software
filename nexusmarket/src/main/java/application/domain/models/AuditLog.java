package application.domain.models;

import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Immutable, append-only audit record of an {@link Operation} (stored in MongoDB). */
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {
    private String auditId;
    private OperationType operationType;
    private LocalDateTime operationDate;
    private User performedBy;
    private SystemRole userRole;
    private EntityReference affectedEntity;
    private Map<String, Object> details;
}
