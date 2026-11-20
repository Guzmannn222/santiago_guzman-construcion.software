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
import application.domain.ports.in.RemoveCartItemUseCase;

@Service
public class RemoveCartItemService extends AuthorizedService implements RemoveCartItemUseCase {

    private final LoadOwnedCartService loadOwnedCartService;
    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public RemoveCartItemService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadOwnedCartService loadOwnedCartService, CartRepositoryPort cartRepositoryPort,
            ProductRepositoryPort productRepositoryPort) {
        super(s, r, t);
        this.loadOwnedCartService = loadOwnedCartService;
        this.cartRepositoryPort = cartRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void removeProduct(User actor, Cart cart, Product product) {
        authorize(actor, SystemRole.BUYER);
        Cart persisted = loadOwnedCartService.execute(actor, cart);
        provided(product, "Product");
        Product persistedProduct = found(productRepositoryPort.findById(idOf(product.getId(), "Product")), "Product",
                product.getId());
        persisted.removeProduct(persistedProduct);
        cartRepositoryPort.update(persisted);
        register(actor, OperationType.CART_ITEM_REMOVED, EntityType.CART, persisted.getId(),
                "productId", persistedProduct.getId());
    }
}
