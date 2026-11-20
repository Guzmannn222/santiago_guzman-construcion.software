package application.domain.services.seller;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.DeactivateSellerUseCase;

/** Administrator deactivates a seller; their published products are suspended. */
@Service
public class DeactivateSellerService extends AuthorizedService implements DeactivateSellerUseCase {

    private final SellerRepositoryPort sellerRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public DeactivateSellerService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            SellerRepositoryPort sellerRepositoryPort, ProductRepositoryPort productRepositoryPort) {
        super(s, r, t);
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void deactivate(User actor, Seller seller) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(seller, "Seller");
        Seller persisted = found(sellerRepositoryPort.findById(idOf(seller.getId(), "Seller")), "Seller", seller.getId());
        persisted.deactivate();
        sellerRepositoryPort.update(persisted);
        for (Product product : productRepositoryPort.findBySellerId(persisted.getId())) {
            if (product.isPublished()) {
                product.suspend();
                productRepositoryPort.update(product);
            }
        }
        register(actor, OperationType.SELLER_DEACTIVATION, EntityType.SELLER, persisted.getId());
    }
}
