package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.User;

/** Input Port: CU-09 cancel an own order before it ships, releasing the reserved stock (role BUYER). */
public interface CancelOrderUseCase {

    void cancelOrder(User actor, Order order);
}
