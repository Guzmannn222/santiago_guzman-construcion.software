package application.domain.ports.out;

import application.domain.models.Buyer;
import application.domain.models.Order;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Order aggregates. */
public interface OrderRepositoryPort {

    Order save(Order order);
    void update(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    List<Order> findByBuyer(Buyer buyer);
}
