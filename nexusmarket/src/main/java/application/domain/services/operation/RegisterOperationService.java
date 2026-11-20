package application.domain.services.operation;

import application.domain.exceptions.InvalidOperationException;
import application.domain.models.Operation;
import application.domain.ports.out.OperationRepositoryPort;
import org.springframework.stereotype.Service;

/** Registers a successful business {@link Operation} through the OperationRepositoryPort. */
@Service
public class RegisterOperationService {

    private final OperationRepositoryPort operationRepositoryPort;

    public RegisterOperationService(OperationRepositoryPort operationRepositoryPort) {
        this.operationRepositoryPort = operationRepositoryPort;
    }

    public Operation execute(Operation operation) {
        if (operation == null) {
            throw new InvalidOperationException("Operation must be provided.");
        }
        if (operation.getOperationType() == null) {
            throw new InvalidOperationException("Operation type must be provided.");
        }
        if (operation.getAffectedEntity() == null) {
            throw new InvalidOperationException("Operation affected entity must be provided.");
        }
        return operationRepositoryPort.save(operation);
    }
}
