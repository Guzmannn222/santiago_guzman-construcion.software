package application.domain.services.cart;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Cart;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import org.springframework.stereotype.Service;

/** Internal helper: reloads a cart and verifies that the actor is the buyer who owns it. */
@Service
public class LoadOwnedCartService {

    private final CartRepositoryPort cartRepositoryPort;

    public LoadOwnedCartService(CartRepositoryPort cartRepositoryPort) {
        this.cartRepositoryPort = cartRepositoryPort;
    }

    public Cart execute(User actor, Cart reference) {
        if (reference == null || reference.getId() == null) {
            throw new EntityNotFoundException("Cart");
        }
        Cart cart = cartRepositoryPort.findById(reference.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart", reference.getId()));
        if (!cart.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only manage their own cart.");
        }
        return cart;
    }
}
