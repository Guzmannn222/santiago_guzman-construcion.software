package application.domain.ports.in;

import application.domain.models.Cart;
import application.domain.models.Product;
import application.domain.models.User;

/** Input Port: CU-08 remove a product from the own cart (role BUYER). */
public interface RemoveCartItemUseCase {

    void removeProduct(User actor, Cart cart, Product product);
}
