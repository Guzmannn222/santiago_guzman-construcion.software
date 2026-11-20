package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class OperationResponseDTO {

    private Long operationId;

    private String operationType;

    private String executionDate;

    private Long performedByUserId;

    private String performedByUsername;

    private String entityType;

    private String entityId;
}
