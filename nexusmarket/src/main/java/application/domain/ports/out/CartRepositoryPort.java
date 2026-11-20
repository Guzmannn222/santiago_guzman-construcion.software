package application.domain.ports.out;

import application.domain.models.Cart;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Cart aggregates. */
public interface CartRepositoryPort {

    Cart save(Cart cart);
    void update(Cart cart);
    Optional<Cart> findById(Long id);
    List<Cart> findAll();
    Optional<Cart> findByBuyerId(Long buyerId);
}
