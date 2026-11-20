package application.domain.services.product;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.CreateProductUseCase;

/** A seller creates a product in their own catalog; it starts suspended until published. */
@Service
public class CreateProductService extends AuthorizedService implements CreateProductUseCase {

    private final SellerRepositoryPort sellerRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public CreateProductService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            SellerRepositoryPort sellerRepositoryPort, ProductRepositoryPort productRepositoryPort) {
        super(s, r, t);
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public Product createProduct(User actor, Seller seller, String name, String description, Money price,
            ProductType type) {
        authorize(actor, SystemRole.SELLER);
        Seller persisted = seller != null && seller.getId() != null
                ? found(sellerRepositoryPort.findById(seller.getId()), "Seller", seller.getId())
                : found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
        if (!persisted.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A seller can only create products in their own catalog.");
        }
        persisted.ensureActive();
        Product product = productRepositoryPort.save(new Product(persisted, name, description, price, type));
        register(actor, OperationType.PRODUCT_CREATION, EntityType.PRODUCT, product.getId(),
                "name", product.getName(), "price", String.valueOf(product.getPrice()));
        return product;
    }
}
