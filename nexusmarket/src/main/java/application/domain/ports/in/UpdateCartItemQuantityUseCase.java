package application.domain.ports.in;

import application.domain.models.Cart;
import application.domain.models.Product;
import application.domain.models.User;

/** Input Port: CU-08 change the quantity of a cart item (role BUYER). */
public interface UpdateCartItemQuantityUseCase {

    void updateQuantity(User actor, Cart cart, Product product, int quantity);
}
