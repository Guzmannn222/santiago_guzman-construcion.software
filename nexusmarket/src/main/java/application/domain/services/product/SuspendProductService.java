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
import application.domain.ports.in.SuspendProductUseCase;

/** A seller or administrator suspends a product. */
@Service
public class SuspendProductService extends AuthorizedService implements SuspendProductUseCase {

    private final LoadManagedProductService loadManagedProductService;
    private final ProductRepositoryPort productRepositoryPort;

    public SuspendProductService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadManagedProductService loadManagedProductService, ProductRepositoryPort productRepositoryPort) {
        super(s, r, t);
        this.loadManagedProductService = loadManagedProductService;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void suspend(User actor, Product product) {
        authorize(actor, SystemRole.SELLER, SystemRole.ADMINISTRATOR);
        Product persisted = loadManagedProductService.execute(actor, product, true);

        persisted.suspend();
        productRepositoryPort.update(persisted);
        register(actor, OperationType.PRODUCT_SUSPENSION, EntityType.PRODUCT, persisted.getId(), "status",
                persisted.getStatus().getCode());
    }
}
