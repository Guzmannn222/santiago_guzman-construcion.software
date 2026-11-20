package application.adapters.rest.dtos.responses;

import java.util.Map;
import lombok.Data;

@Data
public class AuditLogResponseDTO {

    private String auditId;

    private String operationType;

    private String operationDate;

    private Long performedByUserId;

    private String performedByUsername;

    private String userRole;

    private String entityType;

    private String entityId;

    private Map<String, Object> details;
}
