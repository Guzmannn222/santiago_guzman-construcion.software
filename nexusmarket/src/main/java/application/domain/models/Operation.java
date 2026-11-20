package application.domain.models;

import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Significant business action performed by a user over a domain entity (relational log). */
@Getter
@Setter
@NoArgsConstructor
public class Operation {
    private Long operationId;
    private OperationType operationType;
    private LocalDateTime executionDate;
    private User performedBy;
    private EntityReference affectedEntity;
}
