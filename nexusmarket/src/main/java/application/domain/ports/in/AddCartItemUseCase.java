package application.domain.ports.in;

import application.domain.models.Cart;
import application.domain.models.Product;
import application.domain.models.User;

/** Input Port: CU-08 add a published product to the own cart (role BUYER). */
public interface AddCartItemUseCase {

    void addProduct(User actor, Cart cart, Product product, int quantity);
}
