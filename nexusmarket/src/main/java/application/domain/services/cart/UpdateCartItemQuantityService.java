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
import application.domain.ports.in.UpdateCartItemQuantityUseCase;

@Service
public class UpdateCartItemQuantityService extends AuthorizedService implements UpdateCartItemQuantityUseCase {

    private final LoadOwnedCartService loadOwnedCartService;
    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final CartStockGuard cartStockGuard;

    public UpdateCartItemQuantityService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
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
    public void updateQuantity(User actor, Cart cart, Product product, int quantity) {
        authorize(actor, SystemRole.BUYER);
        Cart persisted = loadOwnedCartService.execute(actor, cart);
        provided(product, "Product");
        Product persistedProduct = found(productRepositoryPort.findById(idOf(product.getId(), "Product")), "Product",
                product.getId());
        if (quantity > 0) {
            cartStockGuard.ensureAvailable(persistedProduct, quantity);
        }
        persisted.updateQuantity(persistedProduct, quantity);
        cartRepositoryPort.update(persisted);
        register(actor, OperationType.CART_ITEM_UPDATED, EntityType.CART, persisted.getId(),
                "productId", persistedProduct.getId(), "quantity", quantity);
    }
}
