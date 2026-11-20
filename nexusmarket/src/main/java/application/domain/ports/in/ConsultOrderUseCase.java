package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-09 consult orders. BUYER sees its own, SELLER the ones with its products, the rest of the roles see all. */
public interface ConsultOrderUseCase {

    Order consultOrder(User actor, Order order);
    List<Order> consultAllOrders(User actor);
    List<Order> consultMyOrders(User actor);
}
