package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import application.adapters.persistence.jpa.mappers.OperationJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaOperationRepository;
import application.domain.models.Operation;
import application.domain.ports.out.OperationRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.EntityReference;
import java.util.List;
import org.springframework.stereotype.Repository;

/** Output persistence adapter for Operation backed by Spring Data JPA (MySQL). */
@Repository
public class OperationJpaAdapter implements OperationRepositoryPort {

    private final SpringDataJpaOperationRepository operationRepository;
    private final UserRepositoryPort userRepositoryPort;

    public OperationJpaAdapter(SpringDataJpaOperationRepository operationRepository,
                               UserRepositoryPort userRepositoryPort) {
        this.operationRepository = operationRepository;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Operation save(Operation operation) {
        if (operation == null) {
            return null;
        }
        return toDomain(operationRepository.save(OperationJpaMapper.toEntity(operation)));
    }

    @Override
    public List<Operation> findByUserId(Long userId) {
        return userId == null ? List.of()
                : operationRepository.findByPerformedByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Operation> findByEntity(EntityReference entity) {
        if (entity == null) {
            return List.of();
        }
        return operationRepository.findByAffectedEntityTypeAndAffectedEntityIdentifier(
                entity.getType().getCode(), entity.getIdentifier()).stream().map(this::toDomain).toList();
    }

    private Operation toDomain(OperationJpaEntity entity) {
        return OperationJpaMapper.toDomain(entity, entity.getPerformedByUserId() == null ? null
                : userRepositoryPort.findById(entity.getPerformedByUserId()).orElse(null));
    }
}
