package application.domain.ports.in;

import application.domain.models.Cart;
import application.domain.models.User;

/** Input Port: CU-08 consult a cart. A buyer only sees its own cart. */
public interface ConsultCartUseCase {

    Cart consultCart(User actor, Cart cart);
    Cart consultMyCart(User actor);
}
