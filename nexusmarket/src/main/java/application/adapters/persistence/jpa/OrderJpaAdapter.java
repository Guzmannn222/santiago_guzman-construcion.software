package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.OrderItemJpaEntity;
import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaOrderItemRepository;
import application.adapters.persistence.jpa.repositories.SpringDataJpaOrderRepository;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Output persistence adapter for Order backed by Spring Data JPA (MySQL). */
@Repository
public class OrderJpaAdapter implements OrderRepositoryPort {

    private final SpringDataJpaOrderRepository orderRepository;
    private final SpringDataJpaOrderItemRepository itemRepository;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public OrderJpaAdapter(SpringDataJpaOrderRepository orderRepository,
                           SpringDataJpaOrderItemRepository itemRepository,
                           BuyerRepositoryPort buyerRepositoryPort,
                           WarehouseRepositoryPort warehouseRepositoryPort,
                           ProductRepositoryPort productRepositoryPort) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        if (order == null) {
            return null;
        }
        OrderJpaEntity saved = orderRepository.save(OrderJpaMapper.toEntity(order));
        itemRepository.deleteByOrderId(saved.getId());
        itemRepository.saveAll(OrderJpaMapper.toItemEntities(saved.getId(), order.getItems()));
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void update(Order order) {
        save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return id == null ? Optional.empty() : orderRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Order> findByBuyer(Buyer buyer) {
        if (buyer == null || buyer.getId() == null) {
            return List.of();
        }
        return orderRepository.findByBuyerId(buyer.getId()).stream().map(this::toDomain).toList();
    }

    private Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemJpaEntity row : itemRepository.findByOrderIdOrderByIdAsc(entity.getId())) {
            items.add(OrderJpaMapper.toItemDomain(row, productRepositoryPort.findById(row.getProductId()).orElse(null)));
        }
        return OrderJpaMapper.toDomain(entity,
                buyerRepositoryPort.findById(entity.getBuyerId()).orElse(null),
                entity.getWarehouseId() == null ? null
                        : warehouseRepositoryPort.findById(entity.getWarehouseId()).orElse(null),
                items);
    }
}
