package application.domain.services.cart;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.AddCartItemUseCase;

/** Adds a published product to the buyer's cart (max {@code Cart.MAX_QUANTITY_PER_ITEM} per line). */
@Service
public class AddCartItemService extends AuthorizedService implements AddCartItemUseCase {

    private final LoadOwnedCartService loadOwnedCartService;
    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final CartStockGuard cartStockGuard;

    public AddCartItemService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadOwnedCartService loadOwnedCartService, CartRepositoryPort cartRepositoryPort,
            ProductRepositoryPort productRepositoryPort, CartStockGuard cartStockGuard) {
        super(s, r, t);
        this.cartStockGuard = cartStockGuard;
        this.loadOwnedCartService = loadOwnedCartService;
        this.cartRepositoryPort = cartRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void addProduct(User actor, Cart cart, Product product, int quantity) {
        authorize(actor, SystemRole.BUYER);
        Cart persisted = loadOwnedCartService.execute(actor, cart);
        provided(product, "Product");
        Product persistedProduct = found(productRepositoryPort.findById(idOf(product.getId(), "Product")), "Product",
                product.getId());
        persisted.getBuyer().ensureCanPlaceOrders();
        int alreadyInCart = 0;
        for (CartItem line : persisted.getItems()) {
            if (persistedProduct.getId().equals(line.getProduct().getId())) {
                alreadyInCart = line.getQuantity();
            }
        }
        if (quantity > 0) {
            cartStockGuard.ensureAvailable(persistedProduct, alreadyInCart + quantity);
        }
        persisted.addProduct(persistedProduct, quantity);
        cartRepositoryPort.update(persisted);
        register(actor, OperationType.CART_ITEM_ADDED, EntityType.CART, persisted.getId(),
                "productId", persistedProduct.getId(), "quantity", quantity);
    }
}
