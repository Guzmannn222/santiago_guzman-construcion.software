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
import application.domain.ports.in.DiscontinueProductUseCase;

/** A seller or administrator discontinues a product (final state). */
@Service
public class DiscontinueProductService extends AuthorizedService implements DiscontinueProductUseCase {

    private final LoadManagedProductService loadManagedProductService;
    private final ProductRepositoryPort productRepositoryPort;

    public DiscontinueProductService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadManagedProductService loadManagedProductService, ProductRepositoryPort productRepositoryPort) {
        super(s, r, t);
        this.loadManagedProductService = loadManagedProductService;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void discontinue(User actor, Product product) {
        authorize(actor, SystemRole.SELLER, SystemRole.ADMINISTRATOR);
        Product persisted = loadManagedProductService.execute(actor, product, true);

        persisted.discontinue();
        productRepositoryPort.update(persisted);
        register(actor, OperationType.PRODUCT_DISCONTINUATION, EntityType.PRODUCT, persisted.getId(), "status",
                persisted.getStatus().getCode());
    }
}
