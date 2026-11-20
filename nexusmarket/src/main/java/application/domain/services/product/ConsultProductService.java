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
import application.domain.ports.in.ConsultProductUseCase;

/** Buyers and logistics see published products only; sellers their own; admin/supervisor all. */
@Service
public class ConsultProductService extends AuthorizedService implements ConsultProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ConsultProductService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            ProductRepositoryPort productRepositoryPort, SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.productRepositoryPort = productRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Product consultProduct(User actor, Product product) {
        validateStatus.execute(actor);
        provided(product, "Product");
        Product persisted = found(productRepositoryPort.findById(idOf(product.getId(), "Product")), "Product",
                product.getId());
        SystemRole role = actor.getRole();
        boolean privileged = SystemRole.ADMINISTRATOR.equals(role) || SystemRole.SUPERVISOR.equals(role);
        boolean owner = SystemRole.SELLER.equals(role) && persisted.getSeller() != null
                && persisted.getSeller().isOwnedBy(actor);
        if (!privileged && !owner && !persisted.isPublished()) {
            throw new EntityNotFoundException("Product", persisted.getId());
        }
        return persisted;
    }

    @Override
    public List<Product> consultAllProducts(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return productRepositoryPort.findAll();
    }

    @Override
    public List<Product> consultMyProducts(User actor) {
        authorize(actor, SystemRole.SELLER);
        Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
        return productRepositoryPort.findBySellerId(seller.getId());
    }

    @Override
    public List<Product> consultPublishedProducts(User actor) {
        validateStatus.execute(actor);
        return productRepositoryPort.findByStatus(ProductStatus.PUBLISHED);
    }
}
