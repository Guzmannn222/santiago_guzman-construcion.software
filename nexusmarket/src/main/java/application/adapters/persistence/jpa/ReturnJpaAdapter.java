package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ReturnJpaEntity;
import application.adapters.persistence.jpa.mappers.ReturnJpaMapper;
import application.adapters.persistence.jpa.entities.ReturnItemJpaEntity;
import application.adapters.persistence.jpa.repositories.SpringDataJpaReturnItemRepository;
import application.adapters.persistence.jpa.repositories.SpringDataJpaReturnRepository;
import application.domain.models.Return;
import application.domain.models.ReturnItem;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Output persistence adapter for Return backed by Spring Data JPA (MySQL). */
@Repository
public class ReturnJpaAdapter implements ReturnRepositoryPort {

    private final SpringDataJpaReturnRepository repository;
    private final SpringDataJpaReturnItemRepository itemRepository;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public ReturnJpaAdapter(SpringDataJpaReturnRepository repository,
            SpringDataJpaReturnItemRepository itemRepository,
            OrderRepositoryPort orderRepositoryPort, ProductRepositoryPort productRepositoryPort) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.orderRepositoryPort = orderRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public Return save(Return productReturn) {
        if (productReturn == null) {
            return null;
        }
        ReturnJpaEntity saved = repository.save(ReturnJpaMapper.toEntity(productReturn));
        saveItems(saved.getId(), productReturn);
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void update(Return productReturn) {
        if (productReturn != null) {
            ReturnJpaEntity saved = repository.save(ReturnJpaMapper.toEntity(productReturn));
            saveItems(saved.getId(), productReturn);
        }
    }

    /** Replaces the child rows only when the domain object carries lines (old rows are kept otherwise). */
    private void saveItems(Long returnId, Return productReturn) {
        if (productReturn.getItems() != null && !productReturn.getItems().isEmpty()) {
            itemRepository.deleteByReturnId(returnId);
            itemRepository.saveAll(ReturnJpaMapper.toItemEntities(returnId, productReturn.getItems()));
        }
    }

    @Override
    public Optional<Return> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Return> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Return> findByOrderId(Long orderId) {
        return orderId == null ? List.of() : repository.findByOrderId(orderId).stream().map(this::toDomain).toList();
    }

    private Return toDomain(ReturnJpaEntity entity) {
        Return domain = ReturnJpaMapper.toDomain(entity, orderRepositoryPort.findById(entity.getOrderId()).orElse(null));
        List<ReturnItem> items = new ArrayList<>();
        for (ReturnItemJpaEntity row : itemRepository.findByReturnIdOrderByIdAsc(entity.getId())) {
            items.add(ReturnJpaMapper.toItemDomain(row, productRepositoryPort.findById(row.getProductId()).orElse(null)));
        }
        domain.setItems(items);
        return domain;
    }
}
