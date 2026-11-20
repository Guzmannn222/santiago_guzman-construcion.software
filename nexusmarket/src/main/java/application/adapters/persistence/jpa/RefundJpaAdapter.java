package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import application.adapters.persistence.jpa.mappers.RefundJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaRefundRepository;
import application.domain.models.Refund;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Output persistence adapter for Refund backed by Spring Data JPA (MySQL). */
@Repository
public class RefundJpaAdapter implements RefundRepositoryPort {

    private final SpringDataJpaRefundRepository repository;
    private final ReturnRepositoryPort productReturnRepositoryPort;

    public RefundJpaAdapter(SpringDataJpaRefundRepository repository, ReturnRepositoryPort productReturnRepositoryPort) {
        this.repository = repository;
        this.productReturnRepositoryPort = productReturnRepositoryPort;
    }

    @Override
    public Refund save(Refund refund) {
        if (refund == null) {
            return null;
        }
        return toDomain(repository.save(RefundJpaMapper.toEntity(refund)));
    }

    @Override
    public void update(Refund refund) {
        if (refund != null) {
            repository.save(RefundJpaMapper.toEntity(refund));
        }
    }

    @Override
    public Optional<Refund> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Refund> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Refund> findByReturnId(Long returnId) {
        return returnId == null ? Optional.empty() : repository.findByReturnId(returnId).map(this::toDomain);
    }

    private Refund toDomain(RefundJpaEntity entity) {
        return RefundJpaMapper.toDomain(entity, productReturnRepositoryPort.findById(entity.getReturnId()).orElse(null));
    }
}
