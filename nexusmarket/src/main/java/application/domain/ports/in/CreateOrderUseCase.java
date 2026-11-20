package application.domain.ports.in;

import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.models.Warehouse;

/** Input Port: CU-09 create an order from the own cart, reserving stock. {@code warehouse} may be null: the service picks one able to fulfill the order (role BUYER). */
public interface CreateOrderUseCase {

    Order createOrder(User actor, Cart cart, Warehouse warehouse);
}
