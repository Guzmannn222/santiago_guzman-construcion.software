package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.CartItemJpaEntity;
import application.adapters.persistence.jpa.entities.CartJpaEntity;
import application.adapters.persistence.jpa.mappers.CartJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaCartItemRepository;
import application.adapters.persistence.jpa.repositories.SpringDataJpaCartRepository;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Output persistence adapter for Cart backed by Spring Data JPA (MySQL). */
@Repository
public class CartJpaAdapter implements CartRepositoryPort {

    private final SpringDataJpaCartRepository cartRepository;
    private final SpringDataJpaCartItemRepository itemRepository;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public CartJpaAdapter(SpringDataJpaCartRepository cartRepository,
                          SpringDataJpaCartItemRepository itemRepository,
                          BuyerRepositoryPort buyerRepositoryPort,
                          ProductRepositoryPort productRepositoryPort) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public Cart save(Cart cart) {
        if (cart == null) {
            return null;
        }
        CartJpaEntity saved = cartRepository.save(CartJpaMapper.toEntity(cart));
        itemRepository.deleteByCartId(saved.getId());
        itemRepository.saveAll(CartJpaMapper.toItemEntities(saved.getId(), cart.getItems()));
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void update(Cart cart) {
        save(cart);
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return id == null ? Optional.empty() : cartRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Cart> findByBuyerId(Long buyerId) {
        return buyerId == null ? Optional.empty() : cartRepository.findByBuyerId(buyerId).map(this::toDomain);
    }

    private Cart toDomain(CartJpaEntity entity) {
        List<CartItem> items = new ArrayList<>();
        for (CartItemJpaEntity row : itemRepository.findByCartIdOrderByIdAsc(entity.getId())) {
            items.add(CartJpaMapper.toItemDomain(row, productRepositoryPort.findById(row.getProductId()).orElse(null)));
        }
        return CartJpaMapper.toDomain(entity, buyerRepositoryPort.findById(entity.getBuyerId()).orElse(null), items);
    }
}
