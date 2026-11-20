package application.domain.ports.out;

import application.domain.models.Operation;
import application.domain.valueobjects.EntityReference;
import java.util.List;

/** Output port: relational log of business operations (append-only). */
public interface OperationRepositoryPort {

    Operation save(Operation operation);
    List<Operation> findByUserId(Long userId);
    List<Operation> findByEntity(EntityReference entity);
}
