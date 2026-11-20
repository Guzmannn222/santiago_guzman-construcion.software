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
import application.domain.ports.in.PublishProductUseCase;

/** A seller publishes a product; the seller must be active. */
@Service
public class PublishProductService extends AuthorizedService implements PublishProductUseCase {

    private final LoadManagedProductService loadManagedProductService;
    private final ProductRepositoryPort productRepositoryPort;

    public PublishProductService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadManagedProductService loadManagedProductService, ProductRepositoryPort productRepositoryPort) {
        super(s, r, t);
        this.loadManagedProductService = loadManagedProductService;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void publish(User actor, Product product) {
        authorize(actor, SystemRole.SELLER);
        Product persisted = loadManagedProductService.execute(actor, product, false);
        persisted.getSeller().ensureActive();
        persisted.publish();
        productRepositoryPort.update(persisted);
        register(actor, OperationType.PRODUCT_PUBLICATION, EntityType.PRODUCT, persisted.getId(), "status",
                persisted.getStatus().getCode());
    }
}
