package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.adapters.persistence.jpa.mappers.InvoiceJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInvoiceRepository;
import application.domain.models.Invoice;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Output persistence adapter for Invoice backed by Spring Data JPA (MySQL). */
@Repository
public class InvoiceJpaAdapter implements InvoiceRepositoryPort {

    private final SpringDataJpaInvoiceRepository repository;
    private final OrderRepositoryPort orderRepositoryPort;

    public InvoiceJpaAdapter(SpringDataJpaInvoiceRepository repository, OrderRepositoryPort orderRepositoryPort) {
        this.repository = repository;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        return toDomain(repository.save(InvoiceJpaMapper.toEntity(invoice)));
    }

    @Override
    public void update(Invoice invoice) {
        if (invoice != null) {
            repository.save(InvoiceJpaMapper.toEntity(invoice));
        }
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Invoice> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Invoice> findByOrderId(Long orderId) {
        return orderId == null ? Optional.empty() : repository.findByOrderId(orderId).map(this::toDomain);
    }

    private Invoice toDomain(InvoiceJpaEntity entity) {
        return InvoiceJpaMapper.toDomain(entity, orderRepositoryPort.findById(entity.getOrderId()).orElse(null));
    }
}
